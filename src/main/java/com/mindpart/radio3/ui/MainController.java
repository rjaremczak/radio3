package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import com.mindpart.ui.FxUtils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class);

    @FXML
    ToolBar toolBar;

    @FXML
    ChoiceBox<String> serialPorts;

    @FXML
    Button serialPortsRefresh;

    @FXML
    Button btnConnect;

    @FXML
    AnchorPane mainPane;

    @FXML
    TabPane tabPane;

    @FXML
    Tab deviceTab;

    @FXML
    VBox deviceRuntimePane;

    @FXML
    HBox deviceControlPane;

    @FXML
    Tab sweepTab;

    @FXML
    Tab vnaTab;

    @FXML
    Tab measurementsTab;

    @FXML
    ChoiceBox<VfoType> vfoType;

    @FXML
    Circle mainIndicator;

    @FXML
    VBox configurationBox;

    @FXML
    Label deviceStatus;

    @FXML
    ToggleButton amplifier;

    @FXML
    ToggleButton att6dB;

    @FXML
    ToggleButton att10dB;

    @FXML
    ToggleButton att20dB;

    @FXML
    ToggleGroup vfoOut;

    @FXML
    ToggleButton vfoToSocket;

    @FXML
    ToggleButton vfoToVna;

    @FXML
    TitledPane manifestPane;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private List<Object> nonModalNodes;

    private VnaController vnaController;
    private SweepController sweepController;
    private DashboardController measurementsController;
    private ManifestController manifestController;

    public final UserInterface ui;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
        ui = new UserInterface(radio3.getConfiguration().getLocale());
    }

    public void updateAvailablePorts() {
        availablePortNames.setAll(radio3.availablePorts());
        if (availablePortNames.isEmpty()) {
            FxUtils.disableItems(serialPorts, btnConnect);
            updateDeviceStatus(DeviceStatus.DISCONNECTED);
        } else {
            FxUtils.enableItems(serialPorts, btnConnect);
            serialPorts.getSelectionModel().selectFirst();
            updateDeviceStatus(radio3.getDeviceStatus());
        }
    }

    private void updateOnConnect() {
        btnConnect.setText(ui.text("button.disconnect"));
        FxUtils.enableItems(toolBar, btnConnect, measurementsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        FxUtils.disableItems(serialPorts, serialPortsRefresh, vfoType);
        updateDeviceStatus(DeviceStatus.READY);
        manifestPane.setDisable(false);
        manifestPane.setExpanded(true);
        manifestPane.setCollapsible(false);
    }

    private void updateOnDisconnect(DeviceStatus deviceStatus) {
        btnConnect.setText(ui.text("button.connect"));
        FxUtils.enableItems(serialPorts, serialPortsRefresh, vfoType);
        FxUtils.disableItems(toolBar, measurementsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        btnConnect.setDisable(availablePortNames.isEmpty());
        manifestPane.setCollapsible(true);
        manifestPane.setExpanded(false);
        manifestPane.setDisable(true);
        updateDeviceStatus(deviceStatus);
    }

    private void doConnect() {
        updateDeviceStatus(DeviceStatus.CONNECTING);
        FxUtils.disableItems(serialPortsRefresh, serialPorts);
        Platform.runLater(() -> {
            Response<DeviceConfiguration> response = radio3.connect(serialPorts.getValue(), vfoType.getValue());
            if (response.isOK()) {
                DeviceConfiguration dc = response.getData();
                manifestController.update(dc);
                setUpVfoAtt(dc.hardwareRevision);
                setUpVfoAmp(dc.hardwareRevision);
                requestDeviceState();
                LicenseData ld = requestLicenseData();
                if(checkLicenseData(dc, ld)) {
                    updateOnConnect();
                    return;
                } else {
                    showLicenseViolationDialog(dc, ld);
                }
            }
            radio3.disconnect();
            updateOnDisconnect(DeviceStatus.ERROR);
        });
    }

    private void showLicenseViolationDialog(DeviceConfiguration dc, LicenseData ld) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ui.text("license.alert.title"));
        alert.setHeaderText(ui.text("license.alert.header"));
        alert.setContentText(String.format(ui.text("license.alert.content"), dc.coreUniqueId0, dc.coreUniqueId1, dc.coreUniqueId2));
        alert.showAndWait();
    }

    private boolean checkLicenseData(DeviceConfiguration dc, LicenseData ld) {
        return dc.coreUniqueId0==ld.uniqueId0 && dc.coreUniqueId1==ld.uniqueId1 && dc.coreUniqueId2==ld.uniqueId2;
    }

    void doDisconnect() {
        updateDeviceStatus(DeviceStatus.DISCONNECTING);
        radio3.disconnect();
        updateOnDisconnect(DeviceStatus.DISCONNECTED);
    }

    public void doConnectDisconnect(ActionEvent event) {
        event.consume();
        btnConnect.setDisable(true);
        if (radio3.isConnected()) {
            doDisconnect();
        } else {
            doConnect();
        }
    }

    public void initialize() {
        nonModalNodes = Arrays.asList(deviceTab, sweepTab, vnaTab, measurementsTab);

        serialPortsRefresh.setOnAction((event) -> updateAvailablePorts());
        btnConnect.setOnAction(this::doConnectDisconnect);
        serialPorts.setItems(availablePortNames);

        vnaController = new VnaController(radio3, this);
        sweepController = new SweepController(radio3, this);
        measurementsController = new DashboardController(radio3, ui);
        manifestController = new ManifestController(ui, this::onRefresh);

        sweepTab.setContent(ui.loadFXml(sweepController, "sweepPane.fxml"));
        vnaTab.setContent(ui.loadFXml(vnaController, "vnaPane.fxml"));
        measurementsTab.setContent(ui.loadFXml(measurementsController, "measurements.fxml"));
        manifestPane.setContent(ui.loadFXml(manifestController, "manifest.fxml"));

        initVfoOut();
        initVfoType();
        initVfoAmp();
        initVfoAtt();

        updateAvailablePorts();
        updateOnDisconnect(DeviceStatus.DISCONNECTED);

        tabPane.getSelectionModel().selectedItemProperty().addListener(this::tabSelectionListener);
    }

    private void tabSelectionListener(ObservableValue<? extends Tab> observable, Tab deselectedPane, Tab selectedPane) {
        if(deselectedPane == selectedPane) return;

        if(deselectedPane == measurementsTab) {
            measurementsController.deactivate();
        }

        if(selectedPane == measurementsTab) {
            btnConnect.setDisable(availablePortNames.isEmpty());
            disableVfoOut(false);
            measurementsController.activate();
        } else if(selectedPane == sweepTab) {
            disableVfoOut(true);
        } else if(selectedPane == vnaTab) {
            disableVfoOut(true);
        } else if(selectedPane == deviceTab) {
            disableVfoOut(false);
            requestDeviceState();
        }
    }

    private boolean isDeviceTabSelected() {
        return tabPane.getSelectionModel().getSelectedItem() == deviceTab;
    }

    private void initVfoOut() {
        updateVfoOut(false);
        vfoOut.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null) {
                VfoOut vfoOut = VfoOut.valueOf(((ToggleButton) newValue).getText());
                radio3.sendVfoOutput(vfoOut);
                if(isDeviceTabSelected()) requestDeviceState();
            }
        });
    }

    private void initVfoType() {
        vfoType.getItems().addAll(VfoType.DDS_AD9850, VfoType.DDS_AD9851);
        vfoType.getSelectionModel().select(radio3.getConfiguration().getVfoType());
        vfoType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> radio3.setVfoType(newValue));
    }

    private void initVfoAtt() {
        att6dB.setOnAction(e -> vfoAttListener());
        att10dB.setOnAction(e -> vfoAttListener());
        att20dB.setOnAction(e -> vfoAttListener());
    }

    private void vfoAttListener() {
        radio3.sendVfoAttenuator(att6dB.isSelected(), att10dB.isSelected(), att20dB.isSelected());
        if(isDeviceTabSelected()) requestDeviceState();
    }

    void setUpVfoAtt(HardwareRevision hardwareRevision) {
        att6dB.setSelected(false);
        att10dB.setSelected(false);
        att20dB.setSelected(false);
        FxUtils.setDisabled(!hardwareRevision.isAttenuator(), att6dB, att10dB, att20dB);
    }

    private void initVfoAmp() {
        amplifier.setOnAction(e -> {
            radio3.sendAmplifierEnabled(amplifier.isSelected());
            if(isDeviceTabSelected()) requestDeviceState();
        });
    }

    void setUpVfoAmp(HardwareRevision hardwareRevision) {
        amplifier.setSelected(false);
        amplifier.setDisable(!hardwareRevision.isAmplifier());
    }

    void shutdown() {
        measurementsController.shutdown();
    }

    void requestDeviceState() {
        if(!radio3.isConnected()) return;
        
        Response<DeviceState> response = radio3.readDeviceState();
        if(response.isOK()) {
            DeviceState ds = response.getData();
            manifestController.update(ds);
            updateVfoOut(ds.vfoToVna);
            amplifier.setSelected(ds.amplifier);
            updateVfoAtt(ds.att6dB, ds.att10dB, ds.att20dB);
        }
    }

    private void requestDeviceConfiguration() {
        Response<DeviceConfiguration> response = radio3.readDeviceConfiguration();
        if(response.isOK()) {
            manifestController.update(response.getData());
        }
    }

    private LicenseData requestLicenseData() {
        Response<LicenseData> response = radio3.readLicenseData();
        if(response.isOK()) {
            manifestController.update(response.getData());
            return response.getData();
        } else {
            return null;
        }
    }

    private void updateVfoOut(boolean vfoToVna) {
        vfoOut.selectToggle(vfoToVna ? this.vfoToVna : this.vfoToSocket);
    }

    private void onRefresh() {
        requestDeviceConfiguration();
        requestDeviceState();
        requestLicenseData();
    }

    private void updateVfoAtt(boolean att6dB, boolean att10dB, boolean att20dB) {
        this.att6dB.setSelected(att6dB);
        this.att10dB.setSelected(att10dB);
        this.att20dB.setSelected(att20dB);
    }

    void disableAllExcept(boolean flag, Object element) {
        FxUtils.setDisabled(flag, nonModalNodes.stream().filter(e -> e!=element).toArray());
    }

    void disableVfoOut(boolean flag) {
        FxUtils.setDisabled(flag, vfoToSocket, vfoToVna);
    }

    private void updateMainIndicator(Color color) {
        mainIndicator.setFill(new RadialGradient(-36.87, -0.19, 0.44, 0.41, 0.333, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(0.175, Color.WHITE),
                new Stop(0.65, color),
                new Stop(1.0, color)));
    }

    void updateDeviceStatus(DeviceStatus deviceStatus) {
        updateMainIndicator(deviceStatus.getMainIndicatorColor());
        String portName = radio3.isConnected() ? "("+ radio3.getPortName()+") " : "";
        this.deviceStatus.setText(portName + deviceStatus.format(ui));
    }
}