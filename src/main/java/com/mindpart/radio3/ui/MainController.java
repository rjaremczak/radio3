package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import com.mindpart.types.Frequency;
import com.mindpart.utils.FxUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class);

    @FXML
    ChoiceBox<String> serialPorts;

    @FXML
    Button serialPortsRefresh;

    @FXML
    Button devicePropertiesRefresh;

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
    ChoiceBox<VfoType> vfoType;

    @FXML
    ChoiceBox<VnaMode> vnaMode;

    @FXML
    ChoiceBox<VfoAmpState> vfoAmplifier;

    @FXML
    ChoiceBox<VfoAttenuator> vfoAttenuator;

    @FXML
    ChoiceBox<VfoOut> vfoOutput;

    @FXML
    TableView<Property> devicePropertiesTable;

    @FXML
    ChoiceBox<HardwareRevision> hardwareRevisions;

    @FXML
    Circle mainIndicator;

    @FXML
    VBox configurationBox;

    @FXML
    Label connectionStatus;

    @FXML
    Label deviceStatus;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ScheduledExecutorService continuousSampling = Executors.newSingleThreadScheduledExecutor();
    private List<Object> nonModalNodes;

    private VfoController vfoController;
    private FMeterController fMeterController;
    private LogarithmicProbeController logarithmicProbeController;
    private LinearProbeController linearProbeController;
    private VnaProbeController vnaProbeController;

    private volatile boolean continuousSamplingEnabled = false;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    public void updateAvailablePorts() {
        availablePortNames.setAll(radio3.availablePorts());
        if (availablePortNames.isEmpty()) {
            FxUtils.disableItems(serialPorts, btnConnect);
            connectionStatus.setText(ConnectionStatus.DISCONNECTED.getText());
        } else {
            FxUtils.enableItems(serialPorts, btnConnect);
            serialPorts.getSelectionModel().selectFirst();
            updateConnectionStatus();
        }
    }

    private <T extends ComponentController> void addFeatureBox(T controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("featureBox.fxml"));
        loader.setControllerFactory(clazz -> controller);
        componentsBox.getChildren().add(loader.load());
    }

    private void updateMainIndicator(MainIndicatorState state) {
        mainIndicator.setFill(new RadialGradient(-36.87, -0.19, 0.44, 0.41, 0.333, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(0.175, Color.WHITE),
                new Stop(0.65, state.getColor()),
                new Stop(1.0, state.getColor())));

    }

    private void updateOnConnect() {
        btnConnect.setText("Disconnect");
        FxUtils.enableItems(btnConnect, componentsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        updateConnectionStatus();
        updateMainIndicator(MainIndicatorState.CONNECTED);
    }

    private void updateOnDisconnect() {
        btnConnect.setText("Connect");
        FxUtils.enableItems(btnConnect, serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        FxUtils.disableItems(componentsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        updateConnectionStatus();
        deviceProperties.clear();
        devicePropertiesMap.clear();
        updateMainIndicator(MainIndicatorState.DISCONNECTED);
        updateDeviceStatus(DeviceStatus.UNKNOWN);
    }

    private void doConnect() {
        connectionStatus.setText("connecting...");
        FxUtils.disableItems(serialPortsRefresh, serialPorts);
        updateMainIndicator(MainIndicatorState.PROCESSING);
        Platform.runLater(() -> {
            radio3.connect(serialPorts.getValue());
            if (radio3.isConnected()) {
                updateOnConnect();
                requestDeviceState();
                requestVfoFrequency();
            } else {
                updateOnDisconnect();
            }
        });
    }

    private void updateConnectionStatus() {
        connectionStatus.setText(radio3.getConnectionStatusStr());
    }

    private void doDisconnect() {
        radio3.disconnect();
        updateOnDisconnect();
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

    public void initialize() throws IOException {
        nonModalNodes = Arrays.asList(deviceTab, sweepTab, vnaTab, componentsTab);

        devicePropertiesTable.setItems(deviceProperties);
        devicePropertiesRefresh.setOnAction(event -> onRefresh(event));
        serialPortsRefresh.setOnAction(event -> updateAvailablePorts());
        btnConnect.setOnAction(event -> doConnectDisconnect(event));
        serialPorts.setItems(availablePortNames);

        vfoController = new VfoController(radio3);
        fMeterController = new FMeterController(radio3);
        logarithmicProbeController = new LogarithmicProbeController(radio3);
        linearProbeController = new LinearProbeController(radio3);
        vnaProbeController = new VnaProbeController(radio3);

        addFeatureBox(vfoController);
        addFeatureBox(fMeterController);
        addFeatureBox(logarithmicProbeController);
        addFeatureBox(linearProbeController);
        addFeatureBox(vnaProbeController);

        initVfoOutput();
        initHardwareRevision();
        initVnaMode();
        initVfoType();
        initVfoAmplifier();
        initVfoAttenuator();

        updateOnDisconnect();
        updateConnectionStatus();
        updateAvailablePorts();

        continuousSampling.scheduleWithFixedDelay(() -> { if(continuousSamplingEnabled) { sampleAllProbes(); } }, 200, 200, TimeUnit.MILLISECONDS);

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == componentsTab) {
                requestVfoFrequency();
            } else if(oldValue == componentsTab) {
                //logger.info("leave components tab");
            }
        });
    }

    private void initVfoOutput() {
        vfoOutput.getItems().setAll(VfoOut.values());
        vfoOutput.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(oldValue!=null && newValue!=null) {
                        radio3.getDeviceService().writeVfoOutput(newValue);
                    }
                });
    }

    private void initVfoType() {
        vfoType.getItems().addAll(VfoType.DDS_AD9850, VfoType.DDS_AD9851);
        vfoType.getSelectionModel().select(radio3.getVfoType());
        vfoType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setVfoType(newValue));
    }

    private void initVfoAttenuator() {
        vfoAttenuator.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(oldValue!=null && newValue!=null) {
                        radio3.getDeviceService().writeVfoAttenuator(newValue);
                    }
                });
    }

    void updateVfoAttenuator(HardwareRevision hardwareRevision) {
        vfoAttenuator.getItems().setAll(VfoAttenuator.values());
        vfoAttenuator.getSelectionModel().select(VfoAttenuator.OFF);
        vfoAttenuator.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    private void initHardwareRevision() {
        hardwareRevisions.getItems().addAll(HardwareRevision.values());
        hardwareRevisions.getSelectionModel().select(radio3.getHardwareRevision());
        hardwareRevisions.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setHardwareRevision(newValue));
    }

    private void initVnaMode() {
        vnaMode.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(oldValue!=null && newValue != null) {
                        radio3.getDeviceService().writeVnaMode(newValue);
                    }
                });
    }

    void updateVnaMode(HardwareRevision hardwareRevision) {
        vnaMode.getItems().setAll(VnaMode.values());
        vnaMode.getSelectionModel().select(VnaMode.DIRECTIONAL_COUPLER);
        vnaMode.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    private void initVfoAmplifier() {
        vfoAmplifier.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(oldValue!=null && newValue!=null) {
                        radio3.getDeviceService().writeVfoAmpState(newValue);
                    }
                });
    }

    void updateVfoAmplifier(HardwareRevision hardwareRevision) {
        vfoAmplifier.getItems().setAll(VfoAmpState.values());
        vfoAmplifier.getSelectionModel().select(VfoAmpState.OFF);
        vfoAmplifier.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    void shutdown() {
        continuousSampling.shutdown();
    }

    private void requestDeviceState() {
        Response<DeviceState> deviceStateResponse = radio3.getDeviceService().readDeviceState();
        if(deviceStateResponse.isOK()) updateDeviceState(deviceStateResponse.getData());
    }

    private void requestVfoFrequency() {
        Response<Frequency> response = radio3.getDeviceService().readVfoFrequency();
        if(response.isOK()) vfoController.update(response.getData());
    }

    private void refreshDeviceInfo() {
        Response<DeviceInfo> deviceInfoResponse = radio3.getDeviceService().readDeviceInfo();
        if(deviceInfoResponse.isOK()) updateDeviceInfo(deviceInfoResponse.getData());
    }

    private void onRefresh(ActionEvent event) {
        devicePropertiesRefresh.setDisable(true);
        refreshDeviceInfo();
        requestDeviceState();
        devicePropertiesRefresh.setDisable(false);
    }

    public void sampleAllProbes() {
        Response<ProbesValues> response = radio3.getDeviceService().readAllProbes();
        if(response.isOK()) updateAllProbes(response.getData());
    }

    private void updateAllProbes(ProbesValues probesValues) {
        logarithmicProbeController.update(probesValues.getLogarithmic());
        linearProbeController.update(probesValues.getLinear());
        vnaProbeController.update(probesValues.getVnaResult());
        fMeterController.update(probesValues.getFMeter());
    }

    private void disableGetOnAllProbes(boolean disable) {
        logarithmicProbeController.disableMainButton(disable);
        linearProbeController.disableMainButton(disable);
        vnaProbeController.disableMainButton(disable);
        fMeterController.disableMainButton(disable);
    }

    public void doContinuousSamplingOfAllProbes() {
        if (continuousSamplingOfAllProbesBtn.isSelected()) {
            FxUtils.disableItems(sampleAllProbesBtn, btnConnect, deviceTab, sweepTab, vnaTab);
            disableGetOnAllProbes(true);
            continuousSamplingEnabled = true;
            continuousSamplingOfAllProbesBtn.setText("Stop");
        } else {
            FxUtils.enableItems(sampleAllProbesBtn, btnConnect, deviceTab, sweepTab, vnaTab);
            disableGetOnAllProbes(false);
            continuousSamplingEnabled = false;
            continuousSamplingOfAllProbesBtn.setText("Continuous");
        }
    }

    private void updateDeviceProperties() {
        deviceProperties.setAll(devicePropertiesMap.entrySet().stream().map(e -> new Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    void updateDeviceInfo(DeviceInfo di) {
        devicePropertiesMap.put("Device", di.name);
        devicePropertiesMap.put("Build Id", di.buildId);
        devicePropertiesMap.put("Hardware Revision", di.hardwareRevision.toString());
        devicePropertiesMap.put("VFO", di.vfoType.toString());
        devicePropertiesMap.put("Baud rate", Long.toString(di.baudRate));
        updateDeviceProperties();
        updateVfoAttenuator(di.hardwareRevision);
        updateVnaMode(di.hardwareRevision);
        updateVfoAmplifier(di.hardwareRevision);
    }

    void updateDeviceState(DeviceState ds) {
        devicePropertiesMap.put("Time since reset", ds.timeMs + " ms");
        devicePropertiesMap.put("VFO output", ds.vfoOut.toString());
        devicePropertiesMap.put("VFO amplifier", ds.vfoAmpState.toString());
        devicePropertiesMap.put("VFO attenuator", ds.vfoAttenuator.toString());
        updateDeviceProperties();
        vfoOutput.getSelectionModel().select(ds.vfoOut);
        vfoAmplifier.getSelectionModel().select(ds.vfoAmpState);
        vfoAttenuator.getSelectionModel().select(ds.vfoAttenuator);
    }

    void disableAllExcept(boolean flag, Object element) {
        FxUtils.setDisabledOf(flag, nonModalNodes.stream().filter(e -> e!=element).toArray());
    }

    void updateDeviceStatus(Object o) {
        deviceStatus.setText(o.toString());
    }
}