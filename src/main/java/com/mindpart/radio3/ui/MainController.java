package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.FxUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.util.LinkedHashMap;
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

    @FXML
    ChoiceBox<String> serialPorts;

    @FXML
    Button serialPortsRefresh;

    @FXML
    Button devicePropertiesRefresh;

    @FXML
    Button deviceConnect;

    @FXML
    Label deviceConnectionStatus;

    @FXML
    AnchorPane mainPane;

    @FXML
    Tab deviceTab;

    @FXML
    VBox deviceRuntimePane;

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
    ScrollPane deviceLogScrollPane;

    @FXML
    VBox deviceLogBox;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ScheduledExecutorService continuousSampling = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean continuousSamplingEnabled = false;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    public void updateAvailablePorts() {
        availablePortNames.setAll(radio3.availableSerialPorts());
        if (availablePortNames.isEmpty()) {
            FxUtils.disableItems(serialPorts, deviceConnect);
            deviceConnectionStatus.setText("");
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
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        FxUtils.enableItems(componentsTab, sweepTab, vnaTab, deviceConnect, deviceRuntimePane);
        updateConnectionStatus();
        deviceConnect.setText("Disconnect");
        updateMainIndicator(MainIndicatorState.CONNECTED);
    }

    private void updateOnDisconnect() {
        FxUtils.enableItems(serialPorts, serialPortsRefresh, deviceConnect, hardwareRevisions, vfoType);
        FxUtils.disableItems(componentsTab, sweepTab, vnaTab, deviceRuntimePane);
        updateConnectionStatus();
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        devicePropertiesMap.clear();
        updateMainIndicator(MainIndicatorState.DISCONNECTED);
    }

    private void doConnect() {
        deviceConnectionStatus.setText("connecting...");
        updateMainIndicator(MainIndicatorState.PROCESSING);
        FxUtils.disableItems(deviceConnect, serialPortsRefresh, serialPorts);
        Platform.runLater(() -> {
            radio3.connect(serialPorts.getValue());
            if (radio3.isConnected()) {
                updateOnConnect();
                radio3.requestDeviceState();
                radio3.requestVfoFrequency();
                addDeviceLogEntry("connected");
            } else {
                updateOnDisconnect();
                addDeviceLogEntry("connection error");
            }
        });
    }

    private void updateConnectionStatus() {
        deviceConnectionStatus.setText(radio3.getState().getText());
    }

    private void doDisconnect() {
        radio3.disconnect();
        updateOnDisconnect();
        addDeviceLogEntry("disconnected");
    }

    public void doConnectDisconnect() {
        if (radio3.isConnected()) {
            doDisconnect();
        } else {
            doConnect();
        }
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
        vfoAttenuator.setDisable(true);
        vfoAttenuator.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoAttenuator(newValue));
    }

    private void initHardwareRevision() {
        hardwareRevisions.getItems().addAll(HardwareRevision.values());
        hardwareRevisions.getSelectionModel().select(radio3.getHardwareRevision());
        hardwareRevisions.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setHardwareRevision(newValue));
    }

    private void initVnaMode() {
        vnaMode.getItems().addAll(VnaMode.values());
        vnaMode.getSelectionModel().selectFirst();
        vnaMode.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVnaMode(newValue));
    }

    private void initVfoAmplifier() {
        vfoAmplifier.getItems().addAll(VfoAmplifier.values());
        vfoAmplifier.getSelectionModel().selectFirst();
        vfoAmplifier.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoAmplifier(newValue));
    }

    private void addDeviceLogEntry(String msg) {
        ObservableList<Node> entries = deviceLogBox.getChildren();
        if(entries.size() >= 200) { entries.remove(0); }
        entries.add(new Label(msg));
        deviceLogScrollPane.setVvalue(deviceLogScrollPane.getVmax());
    }

    public void initialize() {
        radio3.bindLogMessageHandler(logMessage -> addDeviceLogEntry(logMessage.getMessage()));

        devicePropertiesTable.setItems(deviceProperties);
        devicePropertiesRefresh.setOnAction(event -> onDevicePropertiesRefresh());
        serialPortsRefresh.setOnAction(event -> updateAvailablePorts());
        deviceConnect.setOnAction(event -> doConnectDisconnect());
        serialPorts.setItems(availablePortNames);
        updateOnDisconnect();
        updateConnectionStatus();
        updateAvailablePorts();
        initVfoOutput();
        initHardwareRevision();
        initVnaMode();
        initVfoType();
        initVfoAmplifier();
        initVfoAttenuator();
        continuousSampling.scheduleWithFixedDelay(() -> { if(continuousSamplingEnabled) { radio3.getProbes(); } }, 200, 200, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        continuousSampling.shutdown();
    }

    public void onDevicePropertiesRefresh() {
        radio3.requestDeviceInfo();
        radio3.requestDeviceState();
    }

    public void handleErrorCode(ErrorCode errorCode) {
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
            FxUtils.disableItems(sampleAllProbesBtn, deviceConnect, sweepTab, vnaTab);
            radio3.disableGetOnAllProbes(true);
            continuousSamplingEnabled = true;

        } else {
            FxUtils.enableItems(sampleAllProbesBtn, deviceConnect, sweepTab, vnaTab);
            radio3.disableGetOnAllProbes(false);
            continuousSamplingEnabled = false;
        }
    }

    private void updateDeviceProperties() {
        deviceProperties.setAll(devicePropertiesMap.entrySet().stream().map(e -> new Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    private void disableHeader(TableView tableView) {
        Pane header = (Pane) tableView.lookup("TableHeaderRow");
        if (header != null && header.isVisible()) {
            header.setMaxHeight(0);
            header.setMinHeight(0);
            header.setPrefHeight(0);
            header.setVisible(false);
            header.setManaged(false);
        }
    }

    public void postDisplayInit() {
        disableHeader(devicePropertiesTable);
        devicePropertiesTable.setFixedCellSize(25);
        devicePropertiesTable.prefHeightProperty().bind(devicePropertiesTable.fixedCellSizeProperty().multiply(Bindings.size(devicePropertiesTable.getItems())));

    }

    public void updateDeviceInfo(DeviceInfo di) {
        devicePropertiesMap.put("Device", di.name);
        devicePropertiesMap.put("Build Id", di.buildId);
        devicePropertiesMap.put("Hardware Revision", di.hardwareRevision.toString());
        devicePropertiesMap.put("VFO", di.vfoType.toString());
        updateDeviceProperties();
        updateVfoAttenuator(di.hardwareRevision);
    }

    public void updateVfoAttenuator(HardwareRevision hardwareRevision) {
        if(hardwareRevision == HardwareRevision.VERSION_2) {
            vfoAttenuator.setDisable(false);
            vfoAttenuator.getItems().setAll(VfoAttenuator.values());
        } else {
            vfoAttenuator.getItems().setAll(VfoAttenuator.LEVEL_0);
            vfoAttenuator.setDisable(true);
        }
        vfoAttenuator.getSelectionModel().selectFirst();
    }

    public void updateDeviceState(DeviceState ds) {
        devicePropertiesMap.put("Continuous sampling", Boolean.toString(ds.probesSampling));
        devicePropertiesMap.put("Sampling period", ds.samplingPeriodMs + " ms");
        devicePropertiesMap.put("Time since reset", ds.timeMs + " ms");
        devicePropertiesMap.put("Analyser's state", ds.analyserState.toString());
        devicePropertiesMap.put("VFO output", ds.vfoOut.toString());
        devicePropertiesMap.put("VFO attenuator", ds.vfoAttenuator.toString());
        updateDeviceProperties();
        vfoOutput.getSelectionModel().select(ds.vfoOut);
        vfoAttenuator.getSelectionModel().select(ds.vfoAttenuator);
    }
}