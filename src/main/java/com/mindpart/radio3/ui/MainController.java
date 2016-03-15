package com.mindpart.radio3.ui;

import com.mindpart.radio3.Radio3;
import com.mindpart.radio3.Status;
import com.mindpart.radio3.device.DeviceInfo;
import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.GainPhase;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController implements Initializable {

    @FXML ChoiceBox<String> deviceSelection;
    @FXML Button deviceSelectionRefresh;
    @FXML Button deviceConnect;
    @FXML Label deviceInfo;

    @FXML Tab deviceInfoTab;

    @FXML TableView<Property> devicePropertiesTable;
    @FXML Button deviceInfoBtn;

    @FXML Tab manualControlTab;

    @FXML TitledPane vfoPane;
    @FXML TextField vfoFrequency;
    @FXML Button vfoSetFrequency;

    @FXML TextField fMeterValue;
    @FXML Button fMeterRead;
    @FXML ToggleButton fMeterStart;

    @FXML TextField logProbeGain;
    @FXML Button logProbeRead;
    @FXML ToggleButton logProbeStart;

    @FXML TextField linProbeGain;
    @FXML Button linProbeRead;
    @FXML ToggleButton linProbeStart;

    @FXML TextField compProbeGain;
    @FXML TextField compProbePhase;
    @FXML Button compProbeRead;
    @FXML ToggleButton compProbeStart;

    private Radio3 radio3;
    private DeviceService deviceService;
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private ScheduledExecutorService pollingExecutor = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean pollFMeter = false;
    private volatile boolean pollLinProbe = false;
    private volatile boolean pollLogProbe = false;
    private volatile boolean pollCompProbe = false;

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

    public void refreshAvailablePorts() {
        availablePortNames.setAll(deviceService.availableSerialPorts());
        if(availablePortNames.isEmpty()) {
            disableNodes(deviceSelection, deviceConnect);
        } else {
            enableNodes(deviceSelection, deviceConnect);
            deviceSelection.getSelectionModel().selectFirst();
        }
    }

    private void refreshOnConnected() {
        disableNodes(deviceSelection, deviceSelectionRefresh);
        enableNodes(deviceInfo, manualControlTab.getContent(), deviceConnect, deviceInfoBtn);
        deviceConnect.setText("Disconnect");
    }

    private void refreshOnDisconnected(String devInfo) {
        enableNodes(deviceSelection, deviceSelectionRefresh, deviceConnect);
        disableNodes(deviceInfo, manualControlTab.getContent(), deviceInfoBtn);
        deviceInfo.setText(devInfo);
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
            if(doDeviceInfoRefresh().isOk()) {
                refreshOnConnected();
            } else {
                deviceService.disconnect();
                refreshOnDisconnected("device is not responding");
            }
        } else {
            deviceInfo.setText(deviceService.getStatus().toString());
        }
    }

    private void doDisconnect() {
        pollFMeter = false;
        if(deviceService.disconnect().isOk()) {
            refreshOnDisconnected("disconnected");
        } else {
            deviceInfo.setText(deviceService.getStatus().toString());
        }
    }

    public void doConnectDisconnect() {
        if(deviceService.isConnected()) { doDisconnect(); } else { doConnect(); };
    }

    public Status doDeviceInfoRefresh() {
        DeviceInfo di = deviceService.readDeviceInfo();
        if(deviceService.getStatus().isOk()) {
            deviceProperties.setAll(
                    new Property("Hardware", di.getHardwareVersionStr()),
                    new Property("Firmware", di.getFirmwareVersionStr()),
                    new Property("VFO", di.getVfoType().name()),
                    new Property("Freq. Meter", di.getFrequencyMeterType().name()),
                    new Property("Timestamp", Long.toString(di.getTimestamp())));
            deviceInfo.setText("connected, firmware: "+di.getFirmwareVersionStr()+" hardware: "+di.getHardwareVersionStr());
        } else {
            deviceInfo.setText("not connected");
            deviceProperties.clear();
        }
        return deviceService.getStatus();
    }

    public void doVfoSetFrequency() {
        int frequency = Integer.parseInt(vfoFrequency.getText());
        deviceService.setVfoFrequency(frequency);
    }

    private String optionalValue(Object value) {
        return value!=null ? value.toString() : "";
    }

    public void doFMeterRead() {
        Long frequency = deviceService.readFrequency();
        fMeterValue.setText(optionalValue(frequency));
    }

    private boolean handlePollStart(ToggleButton startBtn, Button readBtn) {
        if(startBtn.isSelected()) {
            startBtn.setText("Stop");
            readBtn.setDisable(true);
            return true;
        } else {
            startBtn.setText("Start");
            readBtn.setDisable(false);
            return false;
        }
    }

    public void doFMeterStart() {
        pollFMeter = handlePollStart(fMeterStart, fMeterRead);
    }

    public void doLinProbeRead() {
        Double gain = deviceService.readLinProbe();
        linProbeGain.setText(optionalValue(gain));
    }

    public void doLinProbeStart() {
        pollLinProbe = handlePollStart(linProbeStart, linProbeRead);
    }

    public void doLogProbeRead() {
        Double gain = deviceService.readLogProbe();
        logProbeGain.setText(optionalValue(gain));
    }

    public void doLogProbeStart() {
        pollLogProbe = handlePollStart(logProbeStart, logProbeRead);
    }

    public void doCompProbeRead() {
        GainPhase gp = deviceService.readCompProbe();
        compProbeGain.setText(optionalValue(gp.getGain()));
        compProbePhase.setText(optionalValue(gp.getPhase()));
    }

    public void doCompProbeStart() {
        pollCompProbe = handlePollStart(compProbeStart, compProbeRead);
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
        refreshOnDisconnected("disconnected");
        refreshAvailablePorts();
        pollingExecutor = Executors.newSingleThreadScheduledExecutor();
        pollingExecutor.scheduleWithFixedDelay(() -> {
            if(pollFMeter) { doFMeterRead(); }
            if(pollLinProbe) { doLinProbeRead(); }
            if(pollLogProbe) { doLogProbeRead(); }
            if(pollCompProbe) { doCompProbeRead(); }
        }, 2, 1, TimeUnit.SECONDS);
    }

    public void postDisplayInit() {
        disableHeader(devicePropertiesTable);
        devicePropertiesTable.setFixedCellSize(25);
        devicePropertiesTable.prefHeightProperty().bind(devicePropertiesTable.fixedCellSizeProperty().multiply(Bindings.size(devicePropertiesTable.getItems())));

    }
}
