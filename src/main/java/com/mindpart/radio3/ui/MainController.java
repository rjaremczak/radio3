package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.FxUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class);

    @FXML
    ChoiceBox<String> serialPorts;

    @FXML
    Button serialPortsRefresh;

    @FXML
    Button devicePropertiesRefresh;

    @FXML
    Button deviceConnect;

    @FXML
    AnchorPane mainPane;

    @FXML
    TabPane tabPane;

    @FXML
    Tab deviceTab;

    @FXML
    VBox deviceRuntimePane;

    @FXML
    HBox deviceControlPane;

    @FXML
    Tab componentsTab;

    @FXML
    Tab sweepTab;

    @FXML
    Tab vnaTab;

    @FXML
    VBox componentsBox;

    @FXML
    ToggleButton continuousSamplingOfAllProbesBtn;

    @FXML
    Button sampleAllProbesBtn;

    @FXML
    ChoiceBox<VfoType> vfoType;

    @FXML
    ChoiceBox<VnaMode> vnaMode;

    @FXML
    ChoiceBox<VfoAmplifier> vfoAmplifier;

    @FXML
    ChoiceBox<VfoAttenuator> vfoAttenuator;

    @FXML
    ChoiceBox<VfoOut> vfoOutput;

    @FXML
    TableView<Property> devicePropertiesTable;

    @FXML
    ChoiceBox<HardwareRevision> hardwareRevisions;

    @FXML
    Circle mainIndicator;

    @FXML
    ChoiceBox<LogLevel> logLevel;

    @FXML
    VBox configurationBox;

    @FXML
    Label connectionStatus;

    @FXML
    Label deviceStatus;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ScheduledExecutorService continuousSampling = Executors.newSingleThreadScheduledExecutor();
    private List<Object> nonModalNodes;

    private volatile boolean continuousSamplingEnabled = false;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    public void updateAvailablePorts() {
        availablePortNames.setAll(radio3.availableSerialPorts());
        if (availablePortNames.isEmpty()) {
            FxUtils.disableItems(serialPorts, deviceConnect);
            connectionStatus.setText("");
        } else {
            FxUtils.enableItems(serialPorts, deviceConnect);
            serialPorts.getSelectionModel().selectFirst();
            updateConnectionStatus();
        }
    }

    private void updateMainIndicator(MainIndicatorState state) {
        mainIndicator.setFill(new RadialGradient(-36.87, -0.19, 0.44, 0.41, 0.333, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(0.175, Color.WHITE),
                new Stop(0.65, state.getColor()),
                new Stop(1.0, state.getColor())));

    }

    private void updateOnConnect() {
        FxUtils.enableItems(componentsTab, sweepTab, vnaTab, deviceConnect, deviceRuntimePane, deviceControlPane, configurationBox);
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        updateConnectionStatus();
        deviceConnect.setText("Disconnect");
        updateMainIndicator(MainIndicatorState.CONNECTED);
    }

    private void updateOnDisconnect() {
        FxUtils.enableItems(serialPorts, serialPortsRefresh, deviceConnect, hardwareRevisions, vfoType);
        FxUtils.disableItems(componentsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        updateConnectionStatus();
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        devicePropertiesMap.clear();
        updateMainIndicator(MainIndicatorState.DISCONNECTED);
    }

    private void doConnect() {
        connectionStatus.setText("connecting...");
        updateMainIndicator(MainIndicatorState.PROCESSING);
        FxUtils.disableItems(deviceConnect, serialPortsRefresh, serialPorts);
        Platform.runLater(() -> {
            radio3.connect(serialPorts.getValue());
            if (radio3.isConnected()) {
                updateOnConnect();
                radio3.requestDeviceState();
                radio3.requestVfoFrequency();
            } else {
                updateOnDisconnect();
            }
        });
    }

    private void updateConnectionStatus() {
        connectionStatus.setText(radio3.getConnectionStatusStr());
    }

    private void doDisconnect() {
        radio3.disconnect();
        updateOnDisconnect();
    }

    public void doConnectDisconnect() {
        if (radio3.isConnected()) {
            doDisconnect();
        } else {
            doConnect();
        }
    }

    public void initialize() {
        nonModalNodes = Arrays.asList(deviceTab, sweepTab, vnaTab, componentsTab);

        devicePropertiesTable.setItems(deviceProperties);
        devicePropertiesRefresh.setOnAction(event -> onDevicePropertiesRefresh());
        serialPortsRefresh.setOnAction(event -> updateAvailablePorts());
        deviceConnect.setOnAction(event -> doConnectDisconnect());
        serialPorts.setItems(availablePortNames);

        initVfoOutput();
        initHardwareRevision();
        initVnaMode();
        initVfoType();
        initVfoAmplifier();
        initVfoAttenuator();
        initLogLevel();

        updateOnDisconnect();
        updateConnectionStatus();
        updateAvailablePorts();

        continuousSampling.scheduleWithFixedDelay(() -> { if(continuousSamplingEnabled) { radio3.getProbes(); } }, 200, 200, TimeUnit.MILLISECONDS);

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == componentsTab) {
                radio3.requestVfoFrequency();
                //logger.info("enter components tab");
            } else if(oldValue == componentsTab) {
                //logger.info("leave components tab");
            }
        });
    }

    private void initVfoOutput() {
        vfoOutput.getItems().setAll(VfoOut.values());
        vfoOutput.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoOutput(newValue));
    }

    private void initVfoType() {
        vfoType.getItems().addAll(VfoType.DDS_AD9850, VfoType.DDS_AD9851);
        vfoType.getSelectionModel().select(radio3.getVfoType());
        vfoType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoType(newValue));
    }

    private void initVfoAttenuator() {
        vfoAttenuator.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoAttenuator(newValue));
    }

    void updateVfoAttenuator(HardwareRevision hardwareRevision) {
        vfoAttenuator.getItems().setAll(VfoAttenuator.values());
        vfoAttenuator.getSelectionModel().select(VfoAttenuator.OFF);
        vfoAttenuator.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    private void initHardwareRevision() {
        hardwareRevisions.getItems().addAll(HardwareRevision.values());
        hardwareRevisions.getSelectionModel().select(radio3.getHardwareRevision());
        hardwareRevisions.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setHardwareRevision(newValue));
    }

    private void initVnaMode() {
        vnaMode.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.requestVnaMode(newValue));
    }

    void updateVnaMode(HardwareRevision hardwareRevision) {
        vnaMode.getItems().setAll(VnaMode.values());
        vnaMode.getSelectionModel().select(VnaMode.DIRECTIONAL_COUPLER);
        vnaMode.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    private void initVfoAmplifier() {
        vfoAmplifier.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.requestVfoAmplifier(newValue));
    }

    void updateVfoAmplifier(HardwareRevision hardwareRevision) {
        vfoAmplifier.getItems().setAll(VfoAmplifier.values());
        vfoAmplifier.getSelectionModel().select(VfoAmplifier.OFF);
        vfoAmplifier.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    private void initLogLevel() {
        logLevel.getItems().addAll(LogLevel.values());
        logLevel.getSelectionModel().clearSelection();
        logLevel.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.requestLogLevel(newValue));
    }

    void shutdown() {
        continuousSampling.shutdown();
    }

    private void onDevicePropertiesRefresh() {
        radio3.requestDeviceInfo();
        radio3.requestDeviceState();
    }

    void handleErrorCode(ErrorCode errorCode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Device Error");
        alert.setHeaderText("Code: " + errorCode.getFrameCommand());
        if (errorCode.hasAuxCode()) {
            alert.setContentText("auxiliary code: " + errorCode.getAuxCode());
        }
    }

    public void doSampleAllProbes() {
        radio3.getProbes();
    }

    public void doContinuousSamplingOfAllProbes() {
        if (continuousSamplingOfAllProbesBtn.isSelected()) {
            FxUtils.disableItems(sampleAllProbesBtn, deviceConnect, deviceTab, sweepTab, vnaTab);
            radio3.disableGetOnAllProbes(true);
            continuousSamplingEnabled = true;
            continuousSamplingOfAllProbesBtn.setText("Stop");
        } else {
            FxUtils.enableItems(sampleAllProbesBtn, deviceConnect, deviceTab, sweepTab, vnaTab);
            radio3.disableGetOnAllProbes(false);
            continuousSamplingEnabled = false;
            continuousSamplingOfAllProbesBtn.setText("Continuous");
        }
    }

    private void updateDeviceProperties() {
        deviceProperties.setAll(devicePropertiesMap.entrySet().stream().map(e -> new Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    void updateDeviceInfo(DeviceInfo di) {
        devicePropertiesMap.put("Device", di.name);
        devicePropertiesMap.put("Build Id", di.buildId);
        devicePropertiesMap.put("Hardware Revision", di.hardwareRevision.toString());
        devicePropertiesMap.put("VFO", di.vfoType.toString());
        devicePropertiesMap.put("Baud rate", Long.toString(di.baudRate));
        updateDeviceProperties();
        updateVfoAttenuator(di.hardwareRevision);
        updateVnaMode(di.hardwareRevision);
        updateVfoAmplifier(di.hardwareRevision);
    }

    void updateDeviceProperties(DeviceState ds) {
        devicePropertiesMap.put("Time since reset", ds.timeMs + " ms");
        devicePropertiesMap.put("Analyser's state", ds.analyserState.toString());
        devicePropertiesMap.put("VFO output", ds.vfoOut.toString());
        devicePropertiesMap.put("VFO amplifier", ds.vfoAmplifier.toString());
        devicePropertiesMap.put("VFO attenuator", ds.vfoAttenuator.toString());
        devicePropertiesMap.put("Log level", ds.logLevel.toString());
        updateDeviceProperties();
        vfoOutput.getSelectionModel().select(ds.vfoOut);
        vfoAmplifier.getSelectionModel().select(ds.vfoAmplifier);
        vfoAttenuator.getSelectionModel().select(ds.vfoAttenuator);
        logLevel.getSelectionModel().select(ds.logLevel);
    }

    void disableAllExcept(boolean flag, Object element) {
        FxUtils.setDisabledOf(flag, nonModalNodes.stream().filter(e -> e!=element).toArray());
    }

    void updateDeviceStatus(Object o) {
        deviceStatus.setText(o.toString());
    }
}