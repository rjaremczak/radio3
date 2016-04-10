package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.DeviceState;
import com.mindpart.radio3.device.StatusCode;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController implements Initializable {

    @FXML ChoiceBox<String> deviceSelection;
    @FXML Button deviceSelectionRefresh;
    @FXML Button deviceConnect;
    @FXML Label deviceStatus;

    @FXML Tab deviceInfoTab;

    @FXML TableView<Property> devicePropertiesTable;
    @FXML Button deviceInfoBtn;

    @FXML Tab manualControlTab;
    @FXML GridPane manualControlPane;
    @FXML ToggleButton continuousSamplingOfAllProbesBtn;
    @FXML Button sampleAllProbesBtn;

    private Radio3 radio3;
    private DeviceService deviceService;
    private Map<String,String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
        this.deviceService = radio3.getDeviceService();
    }

    private void disableNodes(Node... nodes) {
        for(Node node : nodes) {
            node.setDisable(true);
        }
    }

    private void enableNodes(Node... nodes) {
        for(Node node : nodes) {
            node.setDisable(false);
        }
    }

    public void updateAvailablePorts() {
        availablePortNames.setAll(deviceService.availableSerialPorts());
        if(availablePortNames.isEmpty()) {
            disableNodes(deviceSelection, deviceConnect);
        } else {
            enableNodes(deviceSelection, deviceConnect);
            deviceSelection.getSelectionModel().selectFirst();
        }
    }

    private void updateOnConnect() {
        disableNodes(deviceSelection, deviceSelectionRefresh);
        enableNodes(deviceStatus, manualControlTab.getContent(), deviceConnect, deviceInfoBtn);
        deviceConnect.setText("Disconnect");
    }

    private void updateOnDisconnect(String devInfo) {
        enableNodes(deviceSelection, deviceSelectionRefresh, deviceConnect);
        disableNodes(deviceStatus, manualControlTab.getContent(), deviceInfoBtn);
        deviceStatus.setText(devInfo);
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        devicePropertiesMap.clear();
    }

    private void doConnect() {
        if(deviceService.connect(deviceSelection.getValue()).isOk()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateOnConnect();
            deviceService.getDeviceInfo();
            deviceService.getDeviceState();
            deviceService.getVfoFrequency();
        } else {
            deviceStatus.setText(deviceService.getStatus().toString());
        }
    }

    private void doDisconnect() {
        if(deviceService.disconnect().isOk()) {
            updateOnDisconnect("disconnected");
        } else {
            deviceStatus.setText(deviceService.getStatus().toString());
        }
    }

    public void doConnectDisconnect() {
        if(deviceService.isConnected()) { doDisconnect(); } else { doConnect(); };
    }

    private void updateDeviceProperties() {
        deviceProperties.setAll(devicePropertiesMap.entrySet().stream().map( e -> new Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    public void updateDeviceInfo(DeviceInfo di) {
        devicePropertiesMap.put("Device", di.getName()+" "+di.getVersionStr());
        devicePropertiesMap.put("VFO", di.getVfoName()+" (freq: "+di.getVfoMinFrequency()+" - "+di.getVfoMaxFrequency()+" Hz)");
        devicePropertiesMap.put("FMeter", di.getfMeterName()+" (freq: "+di.getfMeterMinFrequency()+" - "+di.getVfoMaxFrequency()+" Hz)");
        updateDeviceProperties();
        deviceStatus.setText("connected to "+di.getName()+" "+di.getVersionStr());
        if(deviceStatus.isDisable()) {
            updateOnConnect();
        }
    }

    public void updateDeviceState(DeviceState ds) {
        if(continuousSamplingOfAllProbesBtn.isSelected() != ds.isProbesSampling()) {
            continuousSamplingOfAllProbesBtn.setSelected(ds.isProbesSampling());
            doContinuousSamplingOfAllProbes();
        }
        devicePropertiesMap.put("Continuous sampling", Boolean.toString(ds.isProbesSampling()));
        devicePropertiesMap.put("Sampling period", ds.getSamplingPeriodMs()+" ms");
        devicePropertiesMap.put("Time since reset", ds.getTimeMs()+" ms");
        updateDeviceProperties();
    }

    private void disableHeader(TableView tableView) {
        Pane header = (Pane) tableView.lookup("TableHeaderRow");
        if(header!=null && header.isVisible()) {
            header.setMaxHeight(0);
            header.setMinHeight(0);
            header.setPrefHeight(0);
            header.setVisible(false);
            header.setManaged(false);
        }
    }

    public void doDeviceInfo() {
        deviceService.getDeviceInfo();
        deviceService.getDeviceState();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        devicePropertiesTable.setItems(deviceProperties);
        deviceSelection.setItems(availablePortNames);
        updateOnDisconnect("disconnected");
        updateAvailablePorts();
    }

    public void postDisplayInit() {
        disableHeader(devicePropertiesTable);
        devicePropertiesTable.setFixedCellSize(25);
        devicePropertiesTable.prefHeightProperty().bind(devicePropertiesTable.fixedCellSizeProperty().multiply(Bindings.size(devicePropertiesTable.getItems())));

    }

    public void updateStatusCode(StatusCode statusCode) {
        deviceStatus.setText(statusCode.toString());
    }

    public void doSampleAllProbes() {
        deviceService.probesGet();
    }

    public void doContinuousSamplingOfAllProbes() {
        if(continuousSamplingOfAllProbesBtn.isSelected()) {
            sampleAllProbesBtn.setDisable(true);
            deviceService.probesStartSampling();
            radio3.disableGetOnAllProbes(true);

        } else {
            deviceService.probesStopSampling();
            sampleAllProbesBtn.setDisable(false);
            radio3.disableGetOnAllProbes(false);
        }
    }
}