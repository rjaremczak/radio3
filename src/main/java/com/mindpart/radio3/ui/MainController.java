package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceState;
import com.mindpart.radio3.device.ErrorCode;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController {

    @FXML
    ChoiceBox<String> deviceSelection;

    @FXML
    Button deviceSelectionRefresh;

    @FXML
    Button deviceConnect;

    @FXML
    Label deviceConnectionStatus;

    @FXML
    AnchorPane mainPane;

    @FXML
    Tab deviceInfoTab;

    @FXML
    TableView<Property> devicePropertiesTable;

    @FXML
    Button deviceInfoBtn;

    @FXML
    Tab manualControlTab;

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

    private Radio3 radio3;
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private ExecutorService background = Executors.newSingleThreadExecutor();

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    private void disableItems(Object... items) {
        setDisabledOf(true, items);
    }

    private void enableItems(Object... items) {
        setDisabledOf(false, items);
    }

    private void setDisabledOf(boolean flag, Object... items) {
        for (Object item : items) {
            if (item instanceof Node) {
                ((Node) item).setDisable(flag);
            } else if (item instanceof Tab) {
                ((Tab) item).setDisable(flag);
            }
        }
    }

    public void updateAvailablePorts() {
        availablePortNames.setAll(radio3.availableSerialPorts());
        if (availablePortNames.isEmpty()) {
            disableItems(deviceSelection, deviceConnect);
        } else {
            enableItems(deviceSelection, deviceConnect);
            deviceSelection.getSelectionModel().selectFirst();
        }
    }

    private void updateOnConnect() {
        disableItems(deviceSelection, deviceSelectionRefresh);
        enableItems(mainPane);
        deviceConnect.setText("Disconnect");
    }

    private void updateOnDisconnect() {
        enableItems(deviceSelection, deviceSelectionRefresh, deviceConnect);
        disableItems(mainPane);
        updateConnectionStatus();
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        devicePropertiesMap.clear();
    }

    private void doConnect() {
        deviceConnectionStatus.setText("connecting...");
        disableItems(deviceConnect, deviceSelectionRefresh, deviceSelection);
        background.submit(() -> {
            radio3.connect(deviceSelection.getValue());
            Platform.runLater(() -> {
                enableItems(deviceConnect, deviceSelectionRefresh, deviceSelection);
                if (radio3.isConnected()) {
                    updateOnConnect();
                    radio3.getDeviceStateSource().requestData();
                    radio3.getVfoUnit().requestData();
                } else {
                    updateOnDisconnect();
                }
            });
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

    private void updateDeviceProperties() {
        deviceProperties.setAll(devicePropertiesMap.entrySet().stream().map(e -> new Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    public void updateDeviceInfo(DeviceInfo di) {
        devicePropertiesMap.put("Device", di.name);
        devicePropertiesMap.put("Build Id", di.buildId);
        devicePropertiesMap.put("VFO", di.vfo.name + " (freq: " + di.vfo.minFrequency + " to " + di.vfo.maxFrequency + " Hz)");
        devicePropertiesMap.put("FMeter", di.fMeter.name + " (freq: " + di.fMeter.minFrequency + " to " + di.fMeter.maxFrequency + " Hz)");
        devicePropertiesMap.put("Logarithmic probe", di.logProbe.name + " (power: " + di.logProbe.minDBm + " to " + di.logProbe.maxDBm + " dBm)");
        devicePropertiesMap.put("VNA", di.vna.name);
        updateDeviceProperties();
        deviceConnectionStatus.setText("connected to " + di.name);
    }

    public void updateDeviceState(DeviceState ds) {
        if (continuousSamplingOfAllProbesBtn.isSelected() != ds.isProbesSampling()) {
            continuousSamplingOfAllProbesBtn.setSelected(ds.isProbesSampling());
            doContinuousSamplingOfAllProbes();
        }
        devicePropertiesMap.put("Continuous sampling", Boolean.toString(ds.isProbesSampling()));
        devicePropertiesMap.put("Sampling period", ds.getSamplingPeriodMs() + " ms");
        devicePropertiesMap.put("Time since reset", ds.getTimeMs() + " ms");
        updateDeviceProperties();
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

    public void doDeviceInfo() {
        radio3.getDeviceInfoSource().requestData();
        radio3.getDeviceStateSource().requestData();
    }

    @FXML
    public void initialize() {
        devicePropertiesTable.setItems(deviceProperties);
        deviceSelection.setItems(availablePortNames);
        updateOnDisconnect();
        updateConnectionStatus();
        updateAvailablePorts();
    }

    public void shutdown() {
        background.shutdown();
    }

    public void postDisplayInit() {
        disableHeader(devicePropertiesTable);
        devicePropertiesTable.setFixedCellSize(25);
        devicePropertiesTable.prefHeightProperty().bind(devicePropertiesTable.fixedCellSizeProperty().multiply(Bindings.size(devicePropertiesTable.getItems())));

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
            disableItems(sampleAllProbesBtn, deviceConnect, sweepTab, deviceInfoTab, vnaTab);
            radio3.startProbesSampling();
            radio3.disableGetOnAllProbes(true);

        } else {
            radio3.stopProbesSampling();
            enableItems(sampleAllProbesBtn, deviceConnect, sweepTab, deviceInfoTab, vnaTab);
            radio3.disableGetOnAllProbes(false);
        }
    }
}