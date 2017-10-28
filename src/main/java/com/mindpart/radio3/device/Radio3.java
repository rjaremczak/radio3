package com.mindpart.radio3.device;

import com.mindpart.radio3.*;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.config.SweepProfilesService;
import com.mindpart.radio3.ui.DeviceStatus;
import com.mindpart.type.Frequency;
import com.mindpart.bin.Binary;
import com.mindpart.bin.BinaryBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
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

    private SweepProfilesService sweepProfilesService;
    private SweepProfiles sweepProfiles;

    private DataLink dataLink;
    private PingParser pingParser;
    private DeviceStateParser deviceStateParser;
    private VfoParser vfoParser;
    private DeviceInfoParser deviceInfoParser;
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private VnaParser vnaParser;
    private FreqMeterParser freqMeterParser;
    private MultipleProbesParser multipleProbesParser;
    private SweepResponseParser sweepResponseParser;

    private Consumer<Frame> requestHandler;
    private Consumer<Response> responseHandler;
    private ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService keepAliveExecutor = Executors.newSingleThreadScheduledExecutor();
    private AtomicReference<Instant> lastResponseTime = new AtomicReference<>(Instant.MIN);
    private Duration keepAlivePeriod = Duration.ofSeconds(10);

    public Radio3(String appDirectory, Consumer<Frame> requestHandler, Consumer<Response> responseHandler) throws IOException {
        initConfiguration(appDirectory);

        dataLink = new DataLinkJssc();
        logger.info("dataLink: "+dataLink);

        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;

        pingParser = new PingParser();
        deviceStateParser = new DeviceStateParser();
        vfoParser = new VfoParser();
        deviceInfoParser = new DeviceInfoParser();
        logarithmicParser = new LogarithmicParser();
        linearParser = new LinearParser();
        vnaParser = new VnaParser();
        freqMeterParser = new FreqMeterParser(configuration.getFreqMeter());
        multipleProbesParser = new MultipleProbesParser(logarithmicParser, linearParser, vnaParser, freqMeterParser);
        sweepResponseParser = new SweepResponseParser();

        initKeepAlive();
    }

    private void initKeepAlive() {
        if(!configuration.isKeepAlive()) return;
        
        keepAliveExecutor.scheduleWithFixedDelay(() -> {
            if(isConnected() && Duration.between(lastResponseTime.get(), Instant.now()).compareTo(keepAlivePeriod) > 0) {
                sendPing();
            }
        }, keepAlivePeriod.toMillis(), keepAlivePeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void initConfiguration(String appDirectory) throws IOException {
        configurationService = new ConfigurationService(appDirectory);
        configuration = configurationService.load();
        logger.info("UI locale: "+configuration.getLocale());

        sweepProfilesService = new SweepProfilesService(appDirectory);
        sweepProfiles = sweepProfilesService.load();
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
        if(!backgroundExecutor.isTerminated() && !backgroundExecutor.isShutdown()) { backgroundExecutor.submit(task); }
    }

    public void shutdown() {
        backgroundExecutor.shutdown();
        keepAliveExecutor.shutdown();
        disconnect();
    }

    static int buildAvgMode(int avgPasses, int avgSamples) {
        return ((avgPasses - 1) & 0x0f) << 4 | ((avgSamples - 1) & 0x0f);
    }

    public Response<SweepResponse> startAnalyser(long freqStart, long freqStep, int numSteps, int avgPasses, int avgSamples, SweepSignalSource source) {
        BinaryBuilder builder = new BinaryBuilder(14);
        builder.addUInt32(freqStart);
        builder.addUInt32(freqStep);
        builder.addUInt16(numSteps);
        builder.addUInt8(source.ordinal());
        builder.addUInt8(buildAvgMode(avgPasses, avgSamples));

        return performRequest(new Frame(FrameCommand.SWEEP_REQUEST, builder.getBytes()), sweepResponseParser);
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

    public Response<Class<Void>> writeVfoAttenuator(boolean att0, boolean att1, boolean att2) {
        int val = (att0 ? 1 : 0) + (att1 ? 2 : 0) + (att2 ? 4 : 0);
        return performRequest(new Frame(FrameCommand.VFO_ATTENUATOR, Binary.fromUInt8(val)), pingParser);
    }

    public Response<Class<Void>> writeVfoOutput(VfoOut vfoOut) {
        return performRequest(new Frame(vfoOut.getFrameCommand()), pingParser);
    }

    public Response<Class<Void>> writeVnaMode(VnaMode vnaMode) {
        return performRequest(new Frame(FrameCommand.VNA_MODE, Binary.fromUInt8(vnaMode.getCode())), pingParser);
    }

    public Response<Class<Void>> writeVfoAmp(VfoAmp vfoAmp) {
        return performRequest(new Frame(FrameCommand.VFO_AMPLIFIER, Binary.fromUInt8(vfoAmp.getCode())), pingParser);
    }

    public List<String> availablePorts() {
        return dataLink.availablePorts();
    }

    private synchronized <T> Response<T> performRequest(Frame requestFrame, FrameParser<T> frameParser) {
        requestHandler.accept(requestFrame);
        Response<Frame> response = dataLink.request(requestFrame);
        if(response.isOK() && frameParser.recognizes(response.getData())) {
            Response<T> responseOk =  Response.success(frameParser.parse(response.getData()));
            responseHandler.accept(responseOk);
            lastResponseTime.set(Instant.now());
            return responseOk;
        }

        Response<T> responseError = Response.error(response);
        responseHandler.accept(responseError);
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
        return performRequest(new Frame(VNAPROBE_DATA), vnaParser);
    }

    public Response<Frequency> readFMeter() {
        return performRequest(new Frame(FMETER_DATA), freqMeterParser);
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

    public SweepProfiles getSweepProfiles() {
        return sweepProfiles;
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