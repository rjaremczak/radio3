package com.mindpart.radio3.ui;

import com.mindpart.radio3.Radio3;
import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController implements Initializable {
    private Radio3 radio3;
    private DeviceService deviceService;

    @FXML ChoiceBox<String> deviceSelection;
    @FXML Button deviceSelectionRefresh;
    @FXML Button deviceConnect;
    @FXML Button deviceInfoRefresh;
    @FXML Label deviceStatus;
    @FXML TableView<Property> devicePropertiesTable;

    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
        this.deviceService = radio3.getDeviceService();
    }

    private void disableControls(boolean disable, Control... controls) {
        for(Control control : controls) {
            control.setDisable(disable);
        }
    }

    public void refreshAvailablePorts(ActionEvent actionEvent) {
        availablePortNames.setAll(deviceService.availableSerialPorts());
        if(availablePortNames.isEmpty()) {
            disableControls(true, deviceSelection, deviceConnect);
            deviceStatus.setText("no devices found");
        } else {
            disableControls(false, deviceSelection, deviceConnect);
            deviceSelection.getSelectionModel().selectFirst();
            deviceStatus.setText("disconnected");
        }
    }

    public void doConnectDisconnect() {
        if(deviceService.isConnected()) {
            if(deviceService.disconnect().isOk()) {
                disableControls(false, deviceSelection, deviceSelectionRefresh, deviceInfoRefresh, devicePropertiesTable);
                deviceStatus.setText("disconnected");
                deviceConnect.setText("Connect");
            } else {
                deviceStatus.setText(deviceService.getStatus().toString());
            }
        } else {
            if(deviceService.connect(deviceSelection.getValue()).isOk()) {
                disableControls(true, deviceSelection, deviceSelectionRefresh, deviceInfoRefresh, devicePropertiesTable);
                deviceConnect.setText("Disconnect");
                deviceStatus.setText("connected");
            } else {
                deviceStatus.setText(deviceService.getStatus().toString());
            }
        }
    }

    public void doDeviceInfoRefresh() {
        DeviceInfo deviceInfo = deviceService.readDeviceInfo();
        if(deviceService.getStatus().isOk()) {
            deviceProperties.setAll(
                    new Property("hardware", deviceInfo.getHardwareVersionStr()),
                    new Property("firmware", deviceInfo.getFirmwareVersionStr()),
                    new Property("DDS", deviceInfo.getDdsType().name()),
                    new Property("freq. meter", deviceInfo.getFrequencyMeter().name()));
        } else {
            deviceProperties.clear();
            devicePropertiesTable.setPlaceholder(new Label(deviceService.getStatus().toString()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshAvailablePorts(null);
        devicePropertiesTable.setItems(deviceProperties);
        deviceSelection.setItems(availablePortNames);
    }
}
