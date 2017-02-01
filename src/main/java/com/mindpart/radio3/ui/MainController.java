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
    ChoiceBox<DdsOut> ddsOutput;

    @FXML
    TableView<Property> devicePropertiesTable;

    @FXML
    ChoiceBox<HardwareRevision> hardwareRevisions;

    @FXML
    ChoiceBox<VfoType> vfoTypes;

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
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoTypes);
        FxUtils.enableItems(componentsTab, sweepTab, vnaTab, deviceConnect, devicePropertiesRefresh, devicePropertiesTable);
        updateConnectionStatus();
        deviceConnect.setText("Disconnect");

    }

    private void updateOnDisconnect() {
        FxUtils.enableItems(serialPorts, serialPortsRefresh, deviceConnect, hardwareRevisions, vfoTypes);
        FxUtils.disableItems(componentsTab, sweepTab, vnaTab, devicePropertiesRefresh, devicePropertiesTable);
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
                radio3.getDeviceStateSource().requestData();
                radio3.getVfoUnit().requestData();
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

    private void initDdsOut() {
        ddsOutput.getItems().setAll(DdsOut.values());
        ddsOutput.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case VFO: radio3.ddsOutVfo(); break;
                case VNA: radio3.ddsOutVna(); break;
            }
        });
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
        initDdsOut();
        initHardwareRevisions();
        initVfoTypes();
    }

    private void initHardwareRevisions() {
        hardwareRevisions.getItems().addAll(HardwareRevision.values());
        hardwareRevisions.getSelectionModel().selectFirst();
    }

    private void initVfoTypes() {
        vfoTypes.getItems().addAll(VfoType.values());
        vfoTypes.getSelectionModel().selectFirst();
    }

    public void onDevicePropertiesRefresh() {
        radio3.getDeviceInfoSource().requestData();
        radio3.getDeviceStateSource().requestData();
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
        devicePropertiesMap.put("VFO", di.vfo.type.getDescription() + " (freq: " + di.vfo.minFrequency + " to " + di.vfo.maxFrequency + " Hz)");
        updateDeviceProperties();
    }

    public void updateDeviceState(DeviceState ds) {
        devicePropertiesMap.put("Continuous sampling", Boolean.toString(ds.isProbesSampling()));
        devicePropertiesMap.put("Sampling period", ds.getSamplingPeriodMs() + " ms");
        devicePropertiesMap.put("Time since reset", ds.getTimeMs() + " ms");
        devicePropertiesMap.put("Analyser's state", ds.getAnalyserState().toString());
        devicePropertiesMap.put("DDS output", ds.getDdsOut().toString());
        updateDeviceProperties();
        ddsOutput.getSelectionModel().select(ds.getDdsOut());
    }

}