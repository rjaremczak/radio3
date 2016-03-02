package com.mindpart.radio3.ui;

import com.mindpart.radio3.Radio3;
import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController implements Initializable {

    @FXML ChoiceBox<String> deviceSelection;
    @FXML Button deviceSelectionRefresh;
    @FXML Button deviceConnect;
    @FXML Label deviceStatus;
    @FXML TableView<Property> devicePropertiesTable;
    @FXML TitledPane deviceInfoPane;

    @FXML TitledPane vfoPane;
    @FXML TextField vfoFrequency;
    @FXML Button vfoSetFrequency;

    @FXML TextField fMeterValue;
    @FXML Button fMeterRead;
    @FXML ToggleButton fMeterPoll;

    @FXML Button deviceInfoBtn;
    @FXML Label deviceInfo;

    private Radio3 radio3;
    private DeviceService deviceService;
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private ScheduledExecutorService pollingExecutor = Executors.newSingleThreadScheduledExecutor();

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
        this.deviceService = radio3.getDeviceService();
    }

    private void disableControls(Control... controls) {
        for(Control control : controls) {
            control.setDisable(true);
        }
    }

    private void enableControls(Control... controls) {
        for(Control control : controls) {
            control.setDisable(false);
        }
    }

    public void refreshAvailablePorts() {
        availablePortNames.setAll(deviceService.availableSerialPorts());
        if(availablePortNames.isEmpty()) {
            disableControls(deviceSelection, deviceConnect);
        } else {
            enableControls(deviceSelection, deviceConnect);
            deviceSelection.getSelectionModel().selectFirst();
        }
    }

    private void refreshOnConnected() {
        disableControls(deviceSelection, deviceSelectionRefresh);
        enableControls(deviceInfo, deviceInfoBtn);
        deviceStatus.setText("connected");
        deviceConnect.setText("Disconnect");
        deviceInfoPane.setExpanded(true);
    }

    private void refreshOnDisconnected() {
        enableControls(deviceSelection, deviceSelectionRefresh);
        disableControls(deviceInfo, deviceInfoBtn);
        deviceInfo.setText("not connected");
        deviceStatus.setText("disconnected");
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        deviceInfoPane.setExpanded(false);
    }

    private void doConnect() {
        if(deviceService.connect(deviceSelection.getValue()).isOk()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doDeviceInfoRefresh();
            refreshOnConnected();
        } else {
            deviceStatus.setText(deviceService.getStatus().toString());
        }
    }

    private void doDisconnect() {
        if(deviceService.disconnect().isOk()) {
            refreshOnDisconnected();
        } else {
            deviceStatus.setText(deviceService.getStatus().toString());
        }
    }

    public void doConnectDisconnect() {
        if(deviceService.isConnected()) { doDisconnect(); } else { doConnect(); };
    }

    public void doDeviceInfoRefresh() {
        DeviceInfo di = deviceService.readDeviceInfo();
        if(deviceService.getStatus().isOk()) {
            deviceInfo.setText("connected, firmware: "+di.getFirmwareVersionStr()+" hardware: "+di.getHardwareVersionStr());

            deviceProperties.setAll(
                    new Property("Hardware", di.getHardwareVersionStr()),
                    new Property("Firmware", di.getFirmwareVersionStr()),
                    new Property("VFO", di.getVfoType().name()),
                    new Property("Freq. Meter", di.getFrequencyMeterType().name()),
                    new Property("Timestamp", Long.toString(di.getTimestamp())));
        } else {
            deviceInfo.setText("not connected");
            deviceProperties.clear();
        }
    }

    public void doVfoSetFrequency() {
        int frequency = Integer.parseInt(vfoFrequency.getText());
        deviceService.setVfoFrequency(frequency);
    }

    public void doFMeterRead() {
        Long frequency = deviceService.readFrequency();
        fMeterValue.setText(frequency==null ? "" : frequency.toString());
    }

    public void doFMeterPoll() {

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
        doDeviceInfoRefresh();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        devicePropertiesTable.setItems(deviceProperties);
        deviceSelection.setItems(availablePortNames);
        refreshOnDisconnected();
        refreshAvailablePorts();
    }

    public void postDisplayInit() {
        disableHeader(devicePropertiesTable);
        devicePropertiesTable.setFixedCellSize(25);
        devicePropertiesTable.prefHeightProperty().bind(devicePropertiesTable.fixedCellSizeProperty().multiply(Bindings.size(devicePropertiesTable.getItems())));

    }
}
