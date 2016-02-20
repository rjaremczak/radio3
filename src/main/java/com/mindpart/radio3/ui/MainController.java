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
import javafx.scene.layout.Pane;

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
    @FXML Label deviceStatus;
    @FXML TableView<Property> devicePropertiesTable;
    @FXML TitledPane deviceInfoPane;

    @FXML TitledPane ddsPane;
    @FXML TextField ddsFrequency;
    @FXML Button ddsSetFrequency;

    @FXML Button testBtn;

    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();

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
        deviceStatus.setText("connected");
        deviceConnect.setText("Disconnect");
        deviceInfoPane.setExpanded(true);
    }

    private void refreshOnDisconnected() {
        enableControls(deviceSelection, deviceSelectionRefresh);
        deviceStatus.setText("disconnected");
        deviceConnect.setText("Connect");
        deviceProperties.clear();
        devicePropertiesTable.setPlaceholder(new Label(""));
        deviceInfoPane.setExpanded(false);
    }

    private void doConnect() {
        if(deviceService.connect(deviceSelection.getValue()).isOk()) {
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
        DeviceInfo deviceInfo = deviceService.readDeviceInfo();
        if(deviceService.getStatus().isOk()) {
            deviceProperties.setAll(
                    new Property("hardware", deviceInfo.getHardwareVersionStr()),
                    new Property("firmware", deviceInfo.getFirmwareVersionStr()),
                    new Property("DDS", deviceInfo.getDdsType().name()),
                    new Property("freq. meter", deviceInfo.getFrequencyMeter().name()));
        } else {
            deviceProperties.clear();
            devicePropertiesTable.setPlaceholder(new Label("no data"));
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

    public void doTest() {
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
    }
}
