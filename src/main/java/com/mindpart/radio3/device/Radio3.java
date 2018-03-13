package com.mindpart.radio3.device;

import com.mindpart.bin.Binary;
import com.mindpart.bin.BinaryBuilder;
import com.mindpart.radio3.*;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.config.SweepProfilesService;
import com.mindpart.radio3.config.VfoConfig;
import com.mindpart.radio3.ui.DeviceStatus;
import com.mindpart.science.Frequency;
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

import static com.mindpart.radio3.device.FrameCmd.*;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.07
 */
public class Radio3 {
    private static final Logger logger = Logger.getLogger(Radio3.class);

    private final ConfigurationService configurationService;
    private final Configuration configuration;

    private final SweepProfilesService sweepProfilesService;

    private final Consumer<Frame> requestHandler;
    private final Consumer<Response> responseHandler;
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final AtomicReference<Instant> lastResponseTime = new AtomicReference<>(Instant.MIN);

    private final DataLink dataLink;
    private final PingParser pingParser;
    private final DeviceStateParser deviceStateParser;
    private final DeviceConfigurationParser deviceConfigurationParser;
    private final LicenseDataParser licenseDataParser;
    private final VfoParser vfoParser;
    private final ProbesParser probesParser;
    private final SweepResponseParser sweepResponseParser;
    private final int vfoOffset;
    private final SweepProfiles sweepProfiles;

    public Radio3(String appDirectory, Consumer<Frame> requestHandler, Consumer<Response> responseHandler) throws IOException {
        configurationService = new ConfigurationService(appDirectory);
        configuration = configurationService.load();
        logger.info("UI locale: "+configuration.getLocale());

        sweepProfilesService = new SweepProfilesService(appDirectory);
        sweepProfiles = sweepProfilesService.load();

        dataLink = new DataLinkJssc();
        logger.info("dataLink: "+dataLink);

        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;

        pingParser = new PingParser();
        vfoParser = new VfoParser();
        deviceStateParser = new DeviceStateParser();
        deviceConfigurationParser = new DeviceConfigurationParser();
        licenseDataParser = new LicenseDataParser();
        probesParser = new ProbesParser(configuration);
        sweepResponseParser = new SweepResponseParser();

        vfoOffset = configuration.getVfoConfig().getOffset();
    }

    public Response<DeviceConfiguration> connect(String portName, VfoConfig.Type vfoType) {
        try {
            logger.debug("connecting...");
            dataLink.connect(portName);
            sendVfoType(vfoType);
            return readDeviceConfiguration();
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
    }

    public boolean isConnected() {
        return dataLink!=null && dataLink.isOpened();
    }

    public void executeInBackground(Runnable task) {
        if(!backgroundExecutor.isTerminated() && !backgroundExecutor.isShutdown()) { backgroundExecutor.submit(task); }
    }

    public void shutdown() {
        backgroundExecutor.shutdown();
        disconnect();
    }

    static int buildAvgMode(int avgPasses, int avgSamples) {
        return ((avgPasses - 1) & 0x0f) << 4 | ((avgSamples - 1) & 0x0f);
    }

    public Response<SweepResponse> startAnalyser(Frequency freqStart, Frequency freqStep, int numSteps, int avgPasses, int avgSamples, SweepSignalSource source) {
        BinaryBuilder builder = new BinaryBuilder(14);
        builder.addUInt32(freqStart.value);
        builder.addUInt32(freqStep.value);
        builder.addUInt16(numSteps);
        builder.addUInt8(source.ordinal());
        builder.addUInt8(buildAvgMode(avgPasses, avgSamples));

        return performRequest(new Frame(FrameCmd.SWEEP_REQUEST, builder.getBytes()), sweepResponseParser);
    }

    public Response<Class<Void>> sendVfoFrequency(int frequency) {
        return performRequest(new Frame(FrameCmd.SET_VFO_FREQ, Binary.fromUInt32(frequency + vfoOffset)), pingParser);
    }

    public Response<Class<Void>> sendVfoType(VfoConfig.Type vfoType) {
        return performRequest(new Frame(FrameCmd.SET_VFO_TYPE, Binary.fromUInt8(vfoType.ordinal())), pingParser);
    }

    public Response<Class<Void>> sendVfoAttenuator(boolean att0, boolean att1, boolean att2) {
        int val = (att0 ? 1 : 0) + (att1 ? 2 : 0) + (att2 ? 4 : 0);
        return performRequest(new Frame(FrameCmd.SET_ATTENUATOR, Binary.fromUInt8(val)), pingParser);
    }

    public Response<Class<Void>> sendVfoOutput(VfoOut vfoOut) {
        return performRequest(new Frame(vfoOut.getFrameCmd()), pingParser);
    }

    public Response<Class<Void>> sendAmplifierEnabled(boolean enabled) {
        return performRequest(new Frame(FrameCmd.SET_AMPLIFIER, Binary.fromUInt8(enabled ? 1 : 0)), pingParser);
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
        return performRequest(new Frame(GET_DEVICE_STATE), deviceStateParser);
    }

    public Response<DeviceConfiguration> readDeviceConfiguration() {
        return performRequest(new Frame(GET_DEVICE_CONFIGURATION), deviceConfigurationParser);
    }

    public Response<LicenseData> readLicenseData() {
        return performRequest(new Frame(GET_LICENSE_DATA), licenseDataParser);
    }

    public Response<Integer> readVfoFrequency() {
        return performRequest(new Frame(GET_VFO_FREQ), vfoParser);
    }

    public Response<Probes> readAllProbes() {
        return performRequest(new Frame(GET_ALL_PROBES), probesParser);
    }

    public Response<Class<Void>> sendPing() {
        return performRequest(new Frame(PING), pingParser);
    }

    public String getPortName() {
        return dataLink.getPortName();
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

    public void setVfoType(VfoConfig.Type vfoType) {
        if(vfoType != null) {
            configuration.setVfoType(vfoType);
            configurationService.save(configuration);
        }
    }

    public ProbesParser getProbesParser() {
        return probesParser;
    }
}