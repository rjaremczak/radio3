package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Frame;
import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.utils.FxUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.Locale;

public class Radio3FxApp extends Application {
    private static final Logger logger = Logger.getLogger(Radio3FxApp.class);

    private Radio3 radio3;
    private MainController mainController;

    private void requestHandler(Frame frame) {
        Platform.runLater(() -> mainController.updateDeviceStatus(DeviceStatus.PROCESSING));
    }

    private void responseHandler(Response response) {
        Platform.runLater(() -> mainController.updateDeviceStatus(response.isOK() ? DeviceStatus.READY : DeviceStatus.ERROR));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.US);

        radio3 = new Radio3(this::requestHandler, this::responseHandler);
        mainController = new MainController(radio3);

        if(radio3.getConfiguration().getLogLevel() != null) {
            logger.info("root log level: "+ radio3.getConfiguration().getLogLevel());
            Logger.getRootLogger().setLevel(radio3.getConfiguration().getLogLevel());
        }

        primaryStage.setTitle("radio3 by SQ6DGT ("+ radio3.buildId()+")");
        primaryStage.setScene(new Scene(FxUtils.loadPane(mainController, getClass().getResource("main.fxml"))));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mainController.shutdown();
        radio3.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
