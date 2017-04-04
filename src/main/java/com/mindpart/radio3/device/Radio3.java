package com.mindpart.radio3.device;

import com.mindpart.radio3.*;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.ui.DeviceStatus;
import com.mindpart.types.Frequency;
import com.mindpart.utils.Binary;
import com.mindpart.utils.BinaryBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.mindpart.radio3.device.FrameCommand.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class Radio3 {
    private static final Logger logger = Logger.getLogger(Radio3.class);

    private ConfigurationService configurationService;
    private Configuration configuration;
    private DataLink dataLink;
    private PingParser pingParser;
    private DeviceStateParser deviceStateParser;
    private VfoParser vfoParser;
    private DeviceInfoParser deviceInfoParser;
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private VnaParser vnaParser;
    private FMeterParser fMeterParser;
    private MultipleProbesParser multipleProbesParser;
    private AnalyserResponseParser analyserResponseParser;

    private Consumer<Frame> taskOnRequest;
    private Consumer<Response> taskOnResponse;
    private ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    public Radio3(Consumer<Frame> taskOnRequest, Consumer<Response> taskOnResponse) throws IOException {
        initConfiguration();

        dataLink = new DataLinkJssc();
        logger.info("dataLink: "+dataLink);

        this.taskOnRequest = taskOnRequest;
        this.taskOnResponse = taskOnResponse;

        pingParser = new PingParser();
        deviceStateParser = new DeviceStateParser();
        vfoParser = new VfoParser();
        deviceInfoParser = new DeviceInfoParser();
        logarithmicParser = new LogarithmicParser();
        linearParser = new LinearParser();
        vnaParser = new VnaParser();
        fMeterParser = new FMeterParser(configuration.getfMeter());
        multipleProbesParser = new MultipleProbesParser(logarithmicParser, linearParser, vnaParser, fMeterParser);
        analyserResponseParser = new AnalyserResponseParser();
    }

    private void initConfiguration() throws IOException {
        configurationService = new ConfigurationService();
        configurationService.init();
        configuration = configurationService.load();
    }

    public Response<DeviceInfo> connect(String portName, HardwareRevision hardwareRevision, VfoType vfoType) {
        getDeviceInfoParser().resetDeviceInfo();
        try {
            logger.debug("connecting...");
            dataLink.connect(portName);
            writeHardwareRevision(hardwareRevision);
            writeVfoType(vfoType);
            return readDeviceInfo();
        } catch (Exception e) {
            logger.error("connection error", e);
            disconnect();
            return Response.error(e);
        }
    }

    public void disconnect() {
        if(dataLink!=null && dataLink.isOpened()) {
            logger.debug("disconnect");
            dataLink.disconnect();
        }
        deviceInfoParser.resetDeviceInfo();
    }

    public boolean isConnected() {
        return dataLink!=null && dataLink.isOpened();
    }

    public void executeInBackground(Runnable task) {
        backgroundExecutor.submit(task);
    }

    public void shutdown() {
        backgroundExecutor.shutdown();
        disconnect();
    }

    static int buildAvgMode(int avgPasses, int avgSamples) {
        return ((avgPasses - 1) & 0x0f) << 4 | ((avgSamples - 1) & 0x0f);
    }

    public Response<AnalyserResponse> startAnalyser(long freqStart, long freqStep, int numSteps, int avgPasses, int avgSamples, AnalyserDataSource source) {
        BinaryBuilder builder = new BinaryBuilder(14);
        builder.addUInt32(freqStart);
        builder.addUInt32(freqStep);
        builder.addUInt16(numSteps);
        builder.addUInt8(source.ordinal());
        builder.addUInt8(buildAvgMode(avgPasses, avgSamples));

        return performRequest(new Frame(FrameCommand.ANALYSER_REQUEST, builder.getBytes()), analyserResponseParser);
    }

    public Response<Class<Void>> writeVfoFrequency(int frequency) {
        return performRequest(new Frame(FrameCommand.VFO_SET_FREQ, Binary.fromUInt32(frequency)), pingParser);
    }

    public Response<Class<Void>> writeHardwareRevision(HardwareRevision hardwareRevision) {
        return performRequest(new Frame(FrameCommand.DEVICE_HARDWARE_REVISION, Binary.fromUInt8(hardwareRevision.getCode())), pingParser);
    }

    public Response<Class<Void>> writeVfoType(VfoType vfoType) {
        return performRequest(new Frame(FrameCommand.VFO_TYPE, Binary.fromUInt8(vfoType.getCode())), pingParser);
    }

    public Response<Class<Void>> writeVfoAttenuator(VfoAttenuator vfoAttenuator) {
        return performRequest(new Frame(FrameCommand.VFO_ATTENUATOR, Binary.fromUInt8(vfoAttenuator.getCode())), pingParser);
    }

    public Response<Class<Void>> writeVfoOutput(VfoOut vfoOut) {
        return performRequest(new Frame(vfoOut.getFrameCommand()), pingParser);
    }

    public Response<Class<Void>> writeVnaMode(VnaMode vnaMode) {
        return performRequest(new Frame(FrameCommand.VNA_MODE, Binary.fromUInt8(vnaMode.getCode())), pingParser);
    }

    public Response<Class<Void>> writeVfoAmpState(VfoAmpState vfoAmpState) {
        return performRequest(new Frame(FrameCommand.VFO_AMPLIFIER, Binary.fromUInt8(vfoAmpState.getCode())), pingParser);
    }

    public List<String> availablePorts() {
        return dataLink.availablePorts();
    }

    private synchronized <T> Response<T> performRequest(Frame requestFrame, FrameParser<T> frameParser) {
        taskOnRequest.accept(requestFrame);
        Response<Frame> response = dataLink.request(requestFrame);
        if(response.isOK() && frameParser.recognizes(response.getData())) {
            Response<T> responseOk =  Response.success(frameParser.parse(response.getData()));
            taskOnResponse.accept(responseOk);
            return responseOk;
        }

        Response<T> responseError = Response.error(response);
        taskOnResponse.accept(responseError);
        return responseError;
    }

    public Response<DeviceState> readDeviceState() {
        return performRequest(new Frame(DEVICE_GET_STATE), deviceStateParser);
    }

    public Response<Frequency> readVfoFrequency() {
        return performRequest(new Frame(VFO_GET_FREQ), vfoParser);
    }

    public Response<DeviceInfo> readDeviceInfo() {
        return performRequest(new Frame(DEVICE_GET_INFO), deviceInfoParser);
    }

    public Response<Double> readLogProbe() {
        return performRequest(new Frame(LOGPROBE_DATA), logarithmicParser);
    }

    public Response<Double> readLinProbe() {
        return performRequest(new Frame(LINPROBE_DATA), linearParser);
    }

    public Response<VnaResult> readVnaProbe() {
        return performRequest(new Frame(CMPPROBE_DATA), vnaParser);
    }

    public Response<Frequency> readFMeter() {
        return performRequest(new Frame(FMETER_DATA), fMeterParser);
    }

    public Response<ProbesValues> readAllProbes() {
        return performRequest(new Frame(PROBES_DATA), multipleProbesParser);
    }

    public Response<Class<Void>> sendPing() {
        return performRequest(new Frame(PING), pingParser);
    }

    public String getPortName() {
        return dataLink.getPortName();
    }

    public LogarithmicParser getLogarithmicParser() {
        return logarithmicParser;
    }

    public LinearParser getLinearParser() {
        return linearParser;
    }

    public VnaParser getVnaParser() {
        return vnaParser;
    }

    public DeviceInfoParser getDeviceInfoParser() {
        return deviceInfoParser;
    }

    public DeviceStatus getDeviceStatus() {
        return isConnected() ? DeviceStatus.READY : DeviceStatus.DISCONNECTED;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String buildId() {
        return configurationService.getBuildId();
    }

    public VfoType getVfoType() {
        return configuration.getVfoType();
    }

    public void setVfoType(VfoType vfoType) {
        if(vfoType != null) {
            configuration.setVfoType(vfoType);
            configurationService.save(configuration);
        }
    }

    public HardwareRevision getHardwareRevision() {
        return configuration.getHardwareRevision();
    }

    public void setHardwareRevision(HardwareRevision hardwareRevision) {
        if(hardwareRevision != null) {
            configuration.setHardwareRevision(hardwareRevision);
            configurationService.save(configuration);
        }
    }
}