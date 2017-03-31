package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearParser;
import com.mindpart.radio3.LogarithmicParser;
import com.mindpart.radio3.VnaParser;
import com.mindpart.radio3.config.Configuration;
import com.mindpart.radio3.config.ConfigurationService;
import com.mindpart.radio3.device.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mindpart.radio3.ui.ConnectionStatus.*;

public class Radio3 extends Application {
    private static final Logger logger = Logger.getLogger(Radio3.class);

    private ConfigurationService configurationService;
    private DeviceService deviceService;
    private MainController mainController;
    private SweepController sweepController;
    private VnaController vnaController;

    private ConnectionStatus connectionStatus = DISCONNECTED;
    private Configuration configuration;

    ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

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

        deviceService = new DeviceService(configuration);
        mainController = new MainController(this);
        vnaController = new VnaController(this, mainController, configuration.sweepProfiles);
        sweepController = new SweepController(this, mainController, configuration.sweepProfiles);

        primaryStage.setTitle("radio3 by SQ6DGT ("+configurationService.getBuildId()+")");
        primaryStage.setScene(new Scene(loadPane(mainController, "main.fxml")));
        primaryStage.show();

        mainController.sweepTab.setContent(loadPane(sweepController, "sweepPane.fxml"));
        mainController.vnaTab.setContent(loadPane(vnaController, "vnaPane.fxml"));
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

        backgroundExecutor.shutdown();
        mainController.shutdown();
        disconnect();
    }

    public List<String> availablePorts() {
        return deviceService.availablePorts();
    }

    public void connect(String portName) {
        connectionStatus = CONNECTING;
        deviceService.getDeviceInfoParser().resetDeviceInfo();
        if(deviceService.connect(portName).isOk()) {
            deviceService.writeHardwareRevision(configuration.hardwareRevision);
            deviceService.writeVfoType(configuration.vfoType);
            Response<DeviceInfo> deviceInfoResponse = deviceService.readDeviceInfo();
            if(deviceInfoResponse.isOK()) {
                mainController.updateDeviceInfo(deviceInfoResponse.getData());
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
        deviceService.getDeviceInfoParser().resetDeviceInfo();
        sweepController.clear();
        vnaController.clear();
        mainController.updateDeviceStatus("");
    }

    public boolean isConnected() {
        return connectionStatus == CONNECTED;
    }

    public void getProbes() {
        deviceService.readAllProbes();
    }

    public String getConnectionStatusStr() {
        return connectionStatus.getText() + (connectionStatus!=DISCONNECTED ? " ("+deviceService.getDevicePortInfo()+")" : "");
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

    public void executeInBackground(Runnable task) {
        backgroundExecutor.submit(task);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
