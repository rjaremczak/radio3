package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.function.Consumer;

public class Radio3 extends Application {
    private static Logger logger = Logger.getLogger(Radio3.class);

    private DeviceService deviceService;
    private MainController mainController;
    private VfoController vfoController;
    private FMeterController fMeterController;
    private LogarithmicProbeController logarithmicProbeController;
    private LinearProbeController linearProbeController;
    private ComplexProbeController complexProbeController;
    private SweepController sweepController;
    private VnaController vnaController;

    private Consumer<AnalyserData> analyserDataHandler;
    private Consumer<AnalyserState> analyserStateHandler;

    private <T extends FrameParser<U>, U> void bind(T parser, Consumer<U> handler) {
        deviceService.registerBinding(parser, (frameParser, frame) -> {
            U response = ((T)frameParser).parse(frame);
            Platform.runLater(() -> handler.accept(response));
        });
    }

    private <T extends FeatureController> void addFeatureBox(T controller, int column, int row) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("featureBox.fxml"));
        loader.setControllerFactory(clazz -> controller);
        mainController.manualControlPane.add(loader.load(), column, row);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        deviceService = new DeviceService();
        mainController = new MainController(this);
        vfoController = new VfoController(deviceService);
        fMeterController = new FMeterController(deviceService);
        logarithmicProbeController = new LogarithmicProbeController(deviceService);
        linearProbeController = new LinearProbeController(deviceService);
        complexProbeController = new ComplexProbeController(deviceService);
        sweepController = new SweepController(this);
        vnaController = new VnaController(this);

        // should be dynamically updated by currently active controller
        analyserDataHandler = sweepController::updateData;
        analyserStateHandler = sweepController::updateState;

        bind(new LogMessageParser(), this::dumpDeviceLog);
        bind(new DeviceInfoParser(), mainController::updateDeviceInfo);
        bind(new DeviceStateParser(), mainController::updateDeviceState);
        bind(new ErrorCodeParser(), mainController::handleErrorCode);
        bind(new ComplexProbeParser(), complexProbeController::setComplex);
        bind(new LinearProbeParser(), linearProbeController::setGain);
        bind(new LogarithmicProbeParser(), logarithmicProbeController::setGain);
        bind(new FMeterParser(), fMeterController::setFrequency);
        bind(new VfoReadFrequencyParser(), vfoController::setFrequency);
        bind(new ProbesParser(), this::updateAllProbes);
        bind(new AnalyserStateParser(), analyserStateHandler);
        bind(new AnalyserDataParser(), analyserDataHandler);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        loader.setControllerFactory(clazz -> mainController);
        Parent root = loader.load();
        primaryStage.setTitle("Radio 3");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        mainController.postDisplayInit();

        loader = new FXMLLoader(getClass().getResource("sweepPane.fxml"));
        loader.setControllerFactory(clazz -> sweepController);
        mainController.sweepTab.setContent(loader.load());

        loader = new FXMLLoader(getClass().getResource("vnaPane.fxml"));
        loader.setControllerFactory(clazz -> vnaController);
        mainController.vnaTab.setContent(loader.load());

        addFeatureBox(vfoController, 0, 0);
        addFeatureBox(fMeterController, 1, 0);
        addFeatureBox(logarithmicProbeController, 0, 1);
        addFeatureBox(linearProbeController, 1, 1);
        addFeatureBox(complexProbeController, 0, 2);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(deviceService.isConnected()) {
            deviceService.disconnect();
        }
        logger.info("stopped");
    }

    public void updateAllProbes(Probes probes) {
        logarithmicProbeController.setGain(probes.getLogarithmic());
        linearProbeController.setGain(probes.getLinear());
        complexProbeController.setComplex(probes.getComplex());
        fMeterController.setFrequency(probes.getFmeter());
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    protected void disableGetOnAllProbes(boolean disable) {
        logarithmicProbeController.disableMainButton(disable);
        linearProbeController.disableMainButton(disable);
        complexProbeController.disableMainButton(disable);
        fMeterController.disableMainButton(disable);
    }

    private void dumpDeviceLog(LogMessage logMessage) {
        logger.info("DEVICE: "+ logMessage.getMessage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
