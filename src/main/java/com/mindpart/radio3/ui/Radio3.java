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
        vnaController = new VnaController(sweeper, vnaProbe);
        sweepController = new SweepController(sweeper, logarithmicProbe, linearProbe);
        bind(sweeper, deviceService::handleAnalyserData);

        vfoUnit = new VfoUnit(deviceService);
        bind(vfoUnit, vfoController::setFrequency);

        mainController = new MainController(this);

        deviceInfoSource = new DeviceInfoSource(deviceService);
        bind(deviceInfoSource, mainController::updateDeviceInfo);

        deviceStateSource = new DeviceStateSource(deviceService);
        bind(deviceStateSource, mainController::updateDeviceState);

        bind(new LogMessageParser(), this::dumpDeviceLog);
        bind(new ErrorCodeParser(), mainController::handleErrorCode);
        bind(new AnalyserStateParser(), deviceService::handleAnalyserState);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        loader.setControllerFactory(clazz -> mainController);
        Parent root = loader.load();
        primaryStage.setTitle("radio3 by SQ6DGT");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        mainController.postDisplayInit();

        loader = new FXMLLoader(getClass().getResource("sweepPane.fxml"));
        loader.setControllerFactory(clazz -> sweepController);
        mainController.sweepTab.setContent(loader.load());

        loader = new FXMLLoader(getClass().getResource("vnaPane.fxml"));
        loader.setControllerFactory(clazz -> vnaController);
        mainController.vnaTab.setContent(loader.load());

        addFeatureBox(vfoController);
        addFeatureBox(fMeterController);
        addFeatureBox(logarithmicProbeController);
        addFeatureBox(linearProbeController);
        addFeatureBox(vnaProbeController);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        disconnect();
        mainController.shutdown();
        logger.info("stopped");
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
        if(deviceService.connect(portName).isOk()) {
            sleep(200);
            deviceInfoSource.requestData();
            sleep(200);
            if(deviceService.getFramesReceived() > 0) {
                state = CONNECTED;
            } else {
                deviceService.disconnect();
                state = CONNECTION_TIMEOUT;
            }
        } else {
            state = DEVICE_ERROR;
        }
    }

    public void disconnect() {
        deviceService.disconnect();
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

}
