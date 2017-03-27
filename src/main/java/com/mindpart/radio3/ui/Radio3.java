package com.mindpart.radio3.ui;

import com.mindpart.radio3.*;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.device.*;
import com.mindpart.types.Frequency;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static com.mindpart.radio3.ui.ConnectionStatus.*;
import static com.mindpart.radio3.ui.DeviceStatus.UNKNOWN;

public class Radio3 extends Application {
    private static final Logger logger = Logger.getLogger(Radio3.class);

    private PingParser pingParser;
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private VnaParser vnaParser;
    private VfoParser vfoParser;
    private DeviceInfoParser deviceInfoParser;
    private DeviceStateParser deviceStateParser;
    private Sweeper sweeper;
    private FMeterParser fMeterParser;
    private MultipleProbesParser multipleProbesParser;
    private LogMessageParser logMessageParser;

    private ConfigurationService configurationService;
    private DeviceService deviceService;
    private MainController mainController;
    private VfoController vfoController;
    private FMeterController fMeterController;
    private LogarithmicProbeController logarithmicProbeController;
    private LinearProbeController linearProbeController;
    private VnaProbeController vnaProbeController;
    private SweepController sweepController;
    private VnaController vnaController;

    private ConnectionStatus connectionStatus = DISCONNECTED;
    private DeviceStatus deviceStatus = UNKNOWN;
    private Configuration configuration;

    private <T extends FrameParser<U>, U> void bind(T parser, Consumer<U> handler) {
        deviceService.registerBinding(parser, (frameParser, frame) -> {
            U response = ((T)frameParser).parse(frame);
            Platform.runLater(() -> handler.accept(response));
        });
    }

    private <T extends ComponentController> void addFeatureBox(T controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("featureBox.fxml"));
        loader.setControllerFactory(clazz -> controller);
        mainController.componentsBox.getChildren().add(loader.load());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.US);

        configurationService = new ConfigurationService();
        configurationService.init();
        configuration = configurationService.load();

        if(configuration.logLevel != null) {
            logger.info("root log level: "+configuration.logLevel);
            Logger.getRootLogger().setLevel(configuration.logLevel);
        }

        deviceService = new DeviceService((f,b) -> Platform.runLater(() -> mainController.updateDeviceStatus(b ? DeviceStatus.READY : DeviceStatus.ERROR )));
        vfoController = new VfoController(this);

        pingParser = new PingParser();
        bind(pingParser, (v) -> {});

        fMeterParser = new FMeterParser(configuration.fMeter);
        fMeterController = new FMeterController(this);
        bind(fMeterParser, fMeterController::setFrequency);

        logarithmicParser = new LogarithmicParser();
        logarithmicProbeController = new LogarithmicProbeController(this);
        bind(logarithmicParser, logarithmicProbeController::update);

        linearParser = new LinearParser();
        linearProbeController = new LinearProbeController(this);
        bind(linearParser, linearProbeController::update);

        vnaParser = new VnaParser();
        vnaProbeController = new VnaProbeController(this);
        bind(vnaParser, vnaProbeController::update);

        multipleProbesParser = new MultipleProbesParser(logarithmicParser, linearParser, vnaParser, fMeterParser);
        bind(multipleProbesParser, this::updateAllProbes);

        mainController = new MainController(this);

        sweeper = new Sweeper(deviceService);
        vnaController = new VnaController(this, mainController, vnaParser, configuration.sweepProfiles);
        sweepController = new SweepController(this, mainController, logarithmicParser, linearParser, configuration.sweepProfiles);
        bind(sweeper, deviceService::handleAnalyserData);

        vfoParser = new VfoParser();
        bind(vfoParser, vfoController::setFrequency);

        deviceInfoParser = new DeviceInfoParser();
        bind(deviceInfoParser, mainController::updateDeviceInfo);

        deviceStateParser = new DeviceStateParser();
        bind(deviceStateParser, mainController::updateDeviceProperties);

        logMessageParser = new LogMessageParser();
        bind(logMessageParser, msg -> logger.info("DEV: "+msg));

        bind(new ErrorCodeParser(), mainController::handleErrorCode);

        primaryStage.setTitle("radio3 by SQ6DGT ("+configurationService.getBuildId()+")");
        primaryStage.setScene(new Scene(loadPane(mainController, "main.fxml")));
        primaryStage.show();

        mainController.sweepTab.setContent(loadPane(sweepController, "sweepPane.fxml"));
        mainController.vnaTab.setContent(loadPane(vnaController, "vnaPane.fxml"));

        addFeatureBox(vfoController);
        addFeatureBox(fMeterController);
        addFeatureBox(logarithmicProbeController);
        addFeatureBox(linearProbeController);
        addFeatureBox(vnaProbeController);
    }

    public void saveConfiguration() throws IOException {
        configurationService.save(configuration);
    }

    private Parent loadPane(Object controller, String resourceName) {
        FXMLLoader loader  = new FXMLLoader(getClass().getResource(resourceName));
        loader.setControllerFactory(clazz -> controller);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        disconnect();
        mainController.shutdown();
    }

    public void updateAllProbes(ProbeValues probeValues) {
        logarithmicProbeController.update(probeValues.getLogarithmic());
        linearProbeController.update(probeValues.getLinear());
        vnaProbeController.update(probeValues.getVnaResult());
        fMeterController.setFrequency(probeValues.getFMeter());
    }

    protected void disableGetOnAllProbes(boolean disable) {
        logarithmicProbeController.disableMainButton(disable);
        linearProbeController.disableMainButton(disable);
        vnaProbeController.disableMainButton(disable);
        fMeterController.disableMainButton(disable);
    }

    public List<String> availablePorts() {
        return deviceService.availablePorts();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect(String portName) {
        connectionStatus = CONNECTING;
        deviceInfoParser.resetDeviceInfo();
        if(deviceService.connect(portName).isOk()) {
            sleep(200);
            deviceService.setHardwareRevision(configuration.hardwareRevision);
            deviceService.setVfoType(configuration.vfoType);
            deviceService.requestDeviceInfo();
            sleep(200);
            if(deviceInfoParser.isDeviceInfo()) {
                connectionStatus = CONNECTED;

                // TODO: check if HW revision and VFO type are properly set

            } else {
                if(deviceService.getFramesReceived() > 0) {
                    deviceService.disconnect();
                    connectionStatus = DEVICE_ERROR;
                } else {
                    deviceService.disconnect();
                    connectionStatus = CONNECTION_TIMEOUT;
                }
            }
        } else {
            connectionStatus = DEVICE_ERROR;
        }
    }

    public void disconnect() {
        connectionStatus = DISCONNECTED;
        deviceService.disconnect();
        deviceInfoParser.resetDeviceInfo();
        sweepController.clear();
        vnaController.clear();
        mainController.updateDeviceStatus("");
    }

    public boolean isConnected() {
        return connectionStatus == CONNECTED;
    }

    public void getProbes() {
        deviceService.requestMultipleProbesSample();
    }

    public String getConnectionStatusStr() {
        return connectionStatus.getText() + (connectionStatus!=DISCONNECTED ? " ("+deviceService.getDevicePortInfo()+")" : "");
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setVfoOutput(VfoOut vfoOut) {
        if(vfoOut != null) {
            mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
            deviceService.setVfoOutput(vfoOut);
        }
    }

    public void setVfoAttenuator(VfoAttenuator vfoAttenuator) {
        if(vfoAttenuator != null) {
            mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
            deviceService.setVfoAttenuator(vfoAttenuator);
        }
    }

    public void setVfoFrequency(Frequency frequency) {
        if(frequency != null) {
            mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
            deviceService.setVfoFrequency((int) frequency.toHz());
        }
    }

    public VfoType getVfoType() {
        return configuration.vfoType;
    }

    public void setVfoType(VfoType vfoType) {
        if(vfoType != null) {
            configuration.vfoType = vfoType;
            configurationService.save(configuration);
        }
    }

    public HardwareRevision getHardwareRevision() {
        return configuration.hardwareRevision;
    }

    public void setHardwareRevision(HardwareRevision hardwareRevision) {
        if(hardwareRevision != null) {
            configuration.hardwareRevision = hardwareRevision;
            configurationService.save(configuration);
        }
    }

    public void requestVnaMode(VnaMode vnaMode) {
        if(vnaMode != null) {
            mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
            deviceService.setVnaMode(vnaMode);
        }
    }

    public void requestVfoAmplifier(VfoAmplifier vfoAmplifier) {
        if(vfoAmplifier != null) {
            mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
            deviceService.setVfoAmplifier(vfoAmplifier);
        }
    }

    public void requestLogLevel(LogLevel logLevel) {
        if(logLevel != null) {
            mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
            deviceService.setLogLevel(logLevel);
        }
    }

    public void requestDeviceState() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestDeviceState();
    }

    public DeviceState getDeviceState() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        return deviceService.getDeviceState();
    }

    public void requestVfoFrequency() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestVfoFrequency();
    }

    public void requestDeviceInfo() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestDeviceInfo();
    }

    public void requestFMeterSample() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestFMeterSample();
    }

    public void requestLogarithmicProbeSample() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestLogarithmicProbeSample();
    }

    public void requestLinearProbeSample() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestLinearProbeSample();
    }

    public void requestVnaProbeSample() {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.requestVnaProbeSample();
    }

    public void startAnalyser(long fStart, int fStep, SweepQuality quality, AnalyserDataSource source, Consumer<AnalyserData> dataHandler) {
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        deviceService.startAnalyser(fStart, fStep, quality.getSteps(), quality.getAvgPasses(), quality.getAvgSamples(), source, dataHandler);
    }
}
