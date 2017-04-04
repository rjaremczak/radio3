package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearParser;
import com.mindpart.radio3.LogarithmicParser;
import com.mindpart.radio3.VnaParser;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.HardwareRevision;
import com.mindpart.radio3.device.VfoType;
import com.mindpart.utils.FxUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Radio3 extends Application {
    private static final Logger logger = Logger.getLogger(Radio3.class);

    private ConfigurationService configurationService;
    private DeviceService deviceService;
    private MainController mainController;
    private Configuration configuration;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.US);

        configurationService = new ConfigurationService();
        configurationService.init();
        configuration = configurationService.load();

        if(configuration.getLogLevel() != null) {
            logger.info("root log level: "+configuration.getLogLevel());
            Logger.getRootLogger().setLevel(configuration.getLogLevel());
        }

        mainController = new MainController(this);
        deviceService = new DeviceService(configuration, request -> {
            Platform.runLater(() -> mainController.updateDeviceStatus(DeviceStatus.PROCESSING));
        }, response -> {
            Platform.runLater(() -> mainController.updateDeviceStatus(response.isOK() ? DeviceStatus.READY : DeviceStatus.ERROR));
        });

        primaryStage.setTitle("radio3 by SQ6DGT ("+configurationService.getBuildId()+")");
        primaryStage.setScene(new Scene(FxUtils.loadPane(mainController, getClass().getResource("main.fxml"))));
        primaryStage.show();
    }

    public void saveConfiguration() throws IOException {
        configurationService.save(configuration);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mainController.shutdown();
        deviceService.shutdown();
    }

    public List<String> availablePorts() {
        return deviceService.availablePorts();
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

    public LogarithmicParser getLogarithmicParser() {
        return deviceService.getLogarithmicParser();
    }

    public LinearParser getLinearParser() {
        return deviceService.getLinearParser();
    }

    public VnaParser getVnaParser() {
        return deviceService.getVnaParser();
    }

    DeviceService getDeviceService() {
        return deviceService;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
