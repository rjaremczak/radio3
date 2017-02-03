package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import com.mindpart.utils.FxUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
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
    ChoiceBox<VfoOut> vfoOutput;

    @FXML
    ChoiceBox<VfoType> vfoType;

    @FXML
    ChoiceBox<VfoAttenuator> vfoAttenuator;

    @FXML
    TableView<Property> devicePropertiesTable;

    @FXML
    ChoiceBox<HardwareRevision> hardwareRevisions;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();

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

    private void updateOnConnect() {
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        FxUtils.enableItems(componentsTab, sweepTab, vnaTab, deviceConnect, deviceRuntimePane);
        updateConnectionStatus();
        deviceConnect.setText("Disconnect");

    }

    private void updateOnDisconnect() {
        FxUtils.enableItems(serialPorts, serialPortsRefresh, deviceConnect, hardwareRevisions, vfoType);
        FxUtils.disableItems(componentsTab, sweepTab, vnaTab, deviceRuntimePane);
        updateConnectionStatus();
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        devicePropertiesMap.clear();
    }

    private void doConnect() {
        deviceConnectionStatus.setText("connecting...");
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
        deviceConnectionStatus.setText(radio3.getState().getText());
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

    private void initVfoOutput() {
        vfoOutput.getItems().setAll(VfoOut.values());
        vfoOutput.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoOutput(newValue));
    }

    private void initVfoType() {
        vfoType.getItems().addAll(VfoType.values());
        vfoType.getSelectionModel().select(radio3.getVfoType());
        vfoType.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> radio3.setVfoType(newValue)));
    }

    private void initVfoAttenuator() {
        vfoAttenuator.setDisable(true);
        vfoAttenuator.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> radio3.setVfoAttenuator(newValue)));
    }

    private void initHardwareRevision() {
        hardwareRevisions.getItems().addAll(HardwareRevision.values());
        hardwareRevisions.getSelectionModel().select(radio3.getHardwareRevision());
        hardwareRevisions.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> radio3.setHardwareRevision(newValue)));
    }

    public void initialize() {
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
        initVfoType();
        initVfoAttenuator();
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
            radio3.startProbesSampling();
            radio3.disableGetOnAllProbes(true);

        } else {
            radio3.stopProbesSampling();
            FxUtils.enableItems(sampleAllProbesBtn, deviceConnect, sweepTab, vnaTab);
            radio3.disableGetOnAllProbes(false);
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
        if(hardwareRevision == HardwareRevision.PROTOTYPE_2) {
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