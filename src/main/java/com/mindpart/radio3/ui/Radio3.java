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
    private static Logger logger = Logger.getLogger(Radio3.class);

    private LogarithmicProbe logarithmicProbe;
    private LinearProbe linearProbe;
    private VnaProbe vnaProbe;
    private VfoUnit vfoUnit;
    private DeviceInfoSource deviceInfoSource;
    private DeviceStateSource deviceStateSource;
    private Sweeper sweeper;
    private FMeterProbe fMeterProbe;
    private MultipleProbes multipleProbes;

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
        logger.debug("started");
        configurationService = new ConfigurationService();
        configurationService.init();
        Configuration configuration = configurationService.getConfiguration();

        deviceService = new DeviceService();
        vfoController = new VfoController(deviceService);

        fMeterProbe = new FMeterProbe(deviceService, configuration.fMeter);
        fMeterController = new FMeterController(fMeterProbe);
        bind(fMeterProbe, fMeterController::setFrequency);

        logarithmicProbe = new LogarithmicProbe(deviceService);
        logarithmicProbeController = new LogarithmicProbeController(logarithmicProbe);
        bind(logarithmicProbe, logarithmicProbeController::update);

        linearProbe = new LinearProbe(deviceService);
        linearProbeController = new LinearProbeController(linearProbe);
        bind(linearProbe, linearProbeController::update);

        vnaProbe = new VnaProbe(deviceService);
        vnaProbeController = new VnaProbeController(vnaProbe);
        bind(vnaProbe, vnaProbeController::update);

        multipleProbes = new MultipleProbes(deviceService, logarithmicProbe, linearProbe, vnaProbe, fMeterProbe);
        bind(multipleProbes, this::updateAllProbes);

        sweeper = new Sweeper(deviceService);
        vnaController = new VnaController(sweeper, vnaProbe, configuration.sweepProfiles);
        sweepController = new SweepController(sweeper, logarithmicProbe, linearProbe, configuration.sweepProfiles);
        bind(sweeper, deviceService::handleAnalyserData);

        vfoUnit = new VfoUnit(deviceService);
        bind(vfoUnit, vfoController::setFrequency);

        mainController = new MainController(this);

        deviceInfoSource = new DeviceInfoSource(deviceService);
        bind(deviceInfoSource, mainController::updateDeviceInfo);

        deviceStateSource = new DeviceStateSource(deviceService);
        bind(deviceStateSource, deviceState -> {
            mainController.updateDeviceState(deviceState);
            vnaController.updateAnalyserState(deviceState.getAnalyserState());
            sweepController.updateAnalyserState(deviceState.getAnalyserState());
        });


        bind(new LogMessageParser(), this::dumpDeviceLog);
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
        logger.debug("stopped");
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

    public VfoUnit getVfoUnit() {
        return vfoUnit;
    }

    public DeviceInfoSource getDeviceInfoSource() {
        return deviceInfoSource;
    }

    public DeviceStateSource getDeviceStateSource() {
        return deviceStateSource;
    }


    private void dumpDeviceLog(LogMessage logMessage) {
        logger.info("DEVICE: "+ logMessage.getMessage());
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
        deviceInfoSource.resetDeviceInfo();
        if(deviceService.connect(portName).isOk()) {
            sleep(200);
            deviceInfoSource.requestData();
            sleep(200);
            if(deviceInfoSource.isDeviceInfo()) {
                state = CONNECTED;
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
        deviceInfoSource.resetDeviceInfo();
        state = DISCONNECTED;
    }

    public boolean isConnected() {
        return state == CONNECTED;
    }

    public void getProbes() {
        multipleProbes.requestData();
    }

    public void startProbesSampling() {
        multipleProbes.startSampling();
    }

    public void stopProbesSampling() {
        multipleProbes.stopSampling();
    }

    public Radio3State getState() {
        return state;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void ddsOutVfo() {
        deviceService.performRequest(new Frame(FrameCommand.DDS_RELAY_SET_VFO));
    }

    public void ddsOutVna() {
        deviceService.performRequest(new Frame(FrameCommand.DDS_RELAY_SET_VNA));
    }
}
