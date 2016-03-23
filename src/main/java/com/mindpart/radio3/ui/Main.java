package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.function.Consumer;

public class Main extends Application {
    private static Logger logger = Logger.getLogger(Main.class);

    private DeviceService deviceService;
    private MainController mainController;

    private <T extends FrameParser<U>, U> void registerHandler(Class<T> frameParserClass, Consumer<U> handler) {
        deviceService.registerHandler(frameParserClass, (frameParser, frame) -> {
            U response = ((T)frameParser).parse(frame);
            Platform.runLater(() -> handler.accept(response));
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        deviceService = new DeviceService();
        mainController = new MainController(deviceService);

        registerHandler(DeviceInfoParser.class, mainController::updateDeviceInfo);
        registerHandler(StatusCodeParser.class, mainController::updateStatusCode);
        registerHandler(ComplexProbeParser.class, mainController::updateComplexProbe);
        registerHandler(LinearProbeParser.class, mainController::updateLinearProbe);
        registerHandler(LogarithmicProbeParser.class, mainController::updateLogarithmicProbe);
        registerHandler(FMeterParser.class, mainController::updateFMeter);
        registerHandler(VfoReadFrequencyParser.class, mainController::updateVfoFrequency);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        loader.setControllerFactory(clazz -> mainController);
        Parent root = loader.load();
        primaryStage.setTitle("Radio 3");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        mainController.postDisplayInit();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(deviceService.isConnected()) {
            deviceService.disconnect();
        }
        logger.info("stopped");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
