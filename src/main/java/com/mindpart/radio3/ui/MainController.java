package com.mindpart.radio3.ui;

import com.mindpart.radio3.Radio3;
import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController implements Initializable {
    private Radio3 radio3;
    private DeviceService deviceService;

    @FXML public ChoiceBox<String> deviceSelection;
    @FXML public Button deviceSelectionRefresh;
    @FXML public Button deviceConnect;
    @FXML public Label deviceStatus;
    @FXML public Label deviceProperties;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
        this.deviceService = radio3.getDeviceService();
    }

    private void disableControls(boolean disable, Control... controls) {
        for(Control control : controls) {
            control.setDisable(disable);
        }
    }

    public void initDeviceSection(ActionEvent actionEvent) {
        List<String> availablePortNames = deviceService.availableSerialPorts();
        deviceSelection.setItems(FXCollections.observableList(availablePortNames));
        if(availablePortNames.isEmpty()) {
            disableControls(true, deviceSelection, deviceConnect);
            deviceStatus.setText("no devices found");
        } else {
            disableControls(false, deviceSelection, deviceConnect);
            deviceSelection.getSelectionModel().selectFirst();
            deviceStatus.setText("disconnected");
        }
    }

    public void connectDisconnect() {
        if(deviceService.isConnected()) {
            if(deviceService.disconnect().isOk()) {
                disableControls(false, deviceSelection, deviceSelectionRefresh);
                deviceStatus.setText("disconnected");
                deviceConnect.setText("Connect");
            } else {
                deviceStatus.setText(deviceService.getStatus().toString());
            }
        } else {
            if(deviceService.connect(deviceSelection.getValue()).isOk()) {
                disableControls(true, deviceSelection, deviceSelectionRefresh);
                deviceConnect.setText("Disconnect");
                deviceStatus.setText("connected");
            } else {
                deviceStatus.setText(deviceService.getStatus().toString());
            }
        }
    }

    public void readDeviceProperties() {
        DeviceInfo info = deviceService.readDeviceInfo();
        if(deviceService.getStatus().isOk()) {
            deviceProperties.setText(info.getFirmwareVersionStr());
        } else {
            deviceProperties.setText(deviceService.getStatus().toString());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDeviceSection(null);
    }
}
