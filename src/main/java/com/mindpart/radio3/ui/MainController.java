package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.GainPhase;
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
import java.util.ResourceBundle;

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

    private DeviceService deviceService;
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();

    public MainController(DeviceService deviceService) {
        this.deviceService = deviceService;
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
    }

    private void doConnect() {
        if(deviceService.connect(deviceSelection.getValue()).isOk()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateOnConnect();
            deviceService.readDeviceInfo();
            deviceService.readVfoFrequency();
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

    public void updateDeviceInfo(DeviceInfo di) {
        deviceProperties.setAll(
                new Property("Hardware", di.getHardwareVersionStr()),
                new Property("Firmware", di.getFirmwareVersionStr()),
                new Property("VFO", di.getVfoType().name()),
                new Property("Freq. Meter", di.getFrequencyMeterType().name()),
                new Property("Timestamp", Long.toString(di.getTimestamp())));
        deviceStatus.setText("connected, firmware: "+di.getFirmwareVersionStr()+" hardware: "+di.getHardwareVersionStr());
        if(deviceStatus.isDisable()) {
            updateOnConnect();
        }
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
        deviceService.readDeviceInfo();
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
}