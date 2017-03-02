package com.mindpart.radio3.ui;

import com.mindpart.radio3.*;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.device.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static com.mindpart.radio3.ui.Radio3State.*;

public class Radio3 extends Application {
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
    private Consumer<String> logMessageHandler = msg -> {};

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

    private Radio3State state = DISCONNECTED;
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
        configurationService = new ConfigurationService();
        configurationService.init();
        configuration = configurationService.load();

        deviceService = new DeviceService();
        vfoController = new VfoController(deviceService);

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

        sweeper = new Sweeper(deviceService);
        vnaController = new VnaController(sweeper, vnaParser, configuration.sweepProfiles);
        sweepController = new SweepController(sweeper, logarithmicParser, linearParser, configuration.sweepProfiles);
        bind(sweeper, deviceService::handleAnalyserData);

        vfoParser = new VfoParser();
        bind(vfoParser, vfoController::setFrequency);

        mainController = new MainController(this);

        deviceInfoParser = new DeviceInfoParser();
        bind(deviceInfoParser, mainController::updateDeviceInfo);

        deviceStateParser = new DeviceStateParser();
        bind(deviceStateParser, deviceState -> {
            mainController.updateDeviceState(deviceState);
            vnaController.updateAnalyserState(deviceState.analyserState);
            sweepController.updateAnalyserState(deviceState.analyserState);
        });

        logMessageParser = new LogMessageParser();
        bind(logMessageParser, logMessage -> log(logMessage.getMessage()));

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

    public void bindLogMessageHandler(Consumer<LogMessage> handler) {
        bind(logMessageParser, handler);
    }

    public void log(String msg) {
        logMessageHandler.accept(msg);
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
        vnaProbeController.update(probeValues.getComplex());
        fMeterController.setFrequency(probeValues.getFMeter());
    }

    protected void disableGetOnAllProbes(boolean disable) {
        logarithmicProbeController.disableMainButton(disable);
        linearProbeController.disableMainButton(disable);
        vnaProbeController.disableMainButton(disable);
        fMeterController.disableMainButton(disable);
    }

    public List<String> availableSerialPorts() {
        return deviceService.availableSerialPorts();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect(String portName) {
        state = CONNECTING;
        deviceInfoParser.resetDeviceInfo();
        if(deviceService.connect(portName).isOk()) {
            sleep(200);
            deviceService.setHardwareRevision(configuration.hardwareRevision);
            deviceService.setVfoType(configuration.vfoType);
            deviceService.requestDeviceInfo();
            sleep(200);
            if(deviceInfoParser.isDeviceInfo()) {
                state = CONNECTED;

                // TODO: check if HW revision and VFO type are properly set

            } else {
                if(deviceService.getFramesReceived() > 0) {
                    deviceService.disconnect();
                    state = DEVICE_ERROR;
                } else {
                    deviceService.disconnect();
                    state = CONNECTION_TIMEOUT;
                }
            }
        } else {
            state = DEVICE_ERROR;
        }
    }

    public void disconnect() {
        deviceService.disconnect();
        deviceInfoParser.resetDeviceInfo();
        state = DISCONNECTED;
    }

    public boolean isConnected() {
        return state == CONNECTED;
    }

    public void getProbes() {
        deviceService.requestMultipleProbesSample();
    }

    public Radio3State getState() {
        return state;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setVfoOutput(VfoOut vfoOut) {
        if(vfoOut != null) {
            deviceService.setVfoOutput(vfoOut);
            deviceService.requestDeviceState();
        }
    }

    public void setVfoAttenuator(VfoAttenuator vfoAttenuator) {
        if(vfoAttenuator != null) {
            deviceService.setVfoAttenuator(vfoAttenuator);
            deviceService.requestDeviceState();
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

    public void setVnaMode(VnaMode vnaMode) {
        if(vnaMode != null) {
            deviceService.setVnaMode(vnaMode);
            deviceService.requestDeviceState();
        }
    }

    public void setVfoAmplifier(VfoAmplifier vfoAmplifier) {
        if(vfoAmplifier != null) {
            deviceService.setVfoAmplifier(vfoAmplifier);
            deviceService.requestDeviceState();
        }
    }

    public void requestDeviceState() {
        deviceService.requestDeviceState();
    }

    public void requestVfoFrequency() {
        deviceService.requestVfoFrequency();
    }

    public void requestDeviceInfo() {
        deviceService.requestDeviceInfo();
    }

    public void requestFMeterSample() {
        deviceService.requestFMeterSample();
    }

    public void requestLogarithmicProbeSample() {
        deviceService.requestLogarithmicProbeSample();
    }

    public void requestLinearProbeSample() {
        deviceService.requestLinearProbeSample();
    }

    public void requestVnaProbeSample() {
        deviceService.requestVnaProbeSample();
    }

    public void resetDevice() {
        deviceService.requestDeviceReset();
    }
}
