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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    Tab sweepTab;

    @FXML
    Tab vnaTab;

    @FXML
    Tab measurementsTab;

    @FXML
    ChoiceBox<VfoType> vfoType;

    @FXML
    TableView<Property> devicePropertiesTable;

    @FXML
    ChoiceBox<HardwareRevision> hardwareRevisions;

    @FXML
    Circle mainIndicator;

    @FXML
    VBox configurationBox;

    @FXML
    Label deviceStatus;

    @FXML
    ToggleButton vfoAmp;

    @FXML
    ToggleButton vfoAtt0;

    @FXML
    ToggleButton vfoAtt1;

    @FXML
    ToggleButton vfoAtt2;

    @FXML
    ToggleGroup vfoOut;

    @FXML
    ToggleButton vfoOutDirect;

    @FXML
    ToggleButton vfoOutVna;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private List<Object> nonModalNodes;

    private VnaController vnaController;
    private SweepController sweepController;
    private DashboardController measurementsController;

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
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        updateDeviceStatus(DeviceStatus.READY);
    }

    private void updateOnDisconnect(DeviceStatus deviceStatus) {
        btnConnect.setText(ui.text("button.connect"));
        FxUtils.enableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        FxUtils.disableItems(toolBar, measurementsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        btnConnect.setDisable(availablePortNames.isEmpty());
        deviceProperties.clear();
        devicePropertiesMap.clear();
        updateDeviceStatus(deviceStatus);
    }

    private void doConnect() {
        updateDeviceStatus(DeviceStatus.CONNECTING);
        FxUtils.disableItems(serialPortsRefresh, serialPorts);
        Platform.runLater(() -> {
            Response<DeviceInfo> deviceInfoResponse = radio3.connect(serialPorts.getValue(), hardwareRevisions.getValue(), vfoType.getValue());
            if (deviceInfoResponse.isOK()) {
                updateOnConnect();
                DeviceInfo deviceInfo = deviceInfoResponse.getData();
                updateDeviceProperties(deviceInfo);
                setUpVfoAtt(deviceInfo.hardwareRevision);
                setUpVfoAmp(deviceInfo.hardwareRevision);
                requestDeviceState();
            } else {
                radio3.disconnect();
                updateOnDisconnect(DeviceStatus.ERROR);
            }
        });
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

        devicePropertiesTable.setItems(deviceProperties);
        devicePropertiesRefresh.setOnAction(this::onRefresh);
        serialPortsRefresh.setOnAction((event) -> updateAvailablePorts());
        btnConnect.setOnAction(this::doConnectDisconnect);
        serialPorts.setItems(availablePortNames);

        vnaController = new VnaController(radio3, this);
        sweepController = new SweepController(radio3, this);
        measurementsController = new DashboardController(radio3, ui);

        sweepTab.setContent(ui.loadFXml(sweepController, "sweepPane.fxml"));
        vnaTab.setContent(ui.loadFXml(vnaController, "vnaPane.fxml"));
        measurementsTab.setContent(ui.loadFXml(measurementsController, "measurements.fxml"));

        initVfoOut();
        initHardwareRevision();
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
        updateVfoOut(VfoOut.VFO);
        vfoOut.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null) {
                VfoOut vfoOut = VfoOut.valueOf(((ToggleButton) newValue).getText());
                radio3.writeVfoOutput(vfoOut);
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
        vfoAtt0.setOnAction(e -> vfoAttListener());
        vfoAtt1.setOnAction(e -> vfoAttListener());
        vfoAtt2.setOnAction(e -> vfoAttListener());
    }

    private void vfoAttListener() {
        radio3.writeVfoAttenuator(vfoAtt0.isSelected(), vfoAtt1.isSelected(), vfoAtt2.isSelected());
        if(isDeviceTabSelected()) requestDeviceState();
    }

    void setUpVfoAtt(HardwareRevision hardwareRevision) {
        vfoAtt0.setSelected(false);
        vfoAtt1.setSelected(false);
        vfoAtt2.setSelected(false);
        FxUtils.setDisabled(hardwareRevision.isAttenuator(), vfoAtt0, vfoAtt1, vfoAtt2);
    }

    private void initHardwareRevision() {
        hardwareRevisions.getItems().addAll(HardwareRevision.values());
        hardwareRevisions.getSelectionModel().select(radio3.getHardwareRevision());
        hardwareRevisions.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> radio3.setHardwareRevision(newValue));
    }

    private void updateVfoOut(VfoOut mode) {
        switch (mode) {
            case VFO: {
                vfoOut.selectToggle(vfoOutDirect);
                break;
            }
            case VNA: {
                vfoOut.selectToggle(vfoOutVna);
                break;
            }
            default: {
                throw new IllegalArgumentException("not supported: "+mode);
            }
        }
    }

    private void updateVfoAmp(VfoAmp amp) {
        switch (amp) {
            case OFF: {
                vfoAmp.setSelected(false);
                break;
            }
            case ON: {
                vfoAmp.setSelected(true);
                break;
            }
            default: {
                throw new IllegalArgumentException("not supported: "+amp);
            }
        }
    }

    private void initVfoAmp() {
        vfoAmp.setOnAction(e -> {
            radio3.writeVfoAmp(vfoAmp.isSelected() ? VfoAmp.ON : VfoAmp.OFF);
            if(isDeviceTabSelected()) requestDeviceState();
        });
    }

    void setUpVfoAmp(HardwareRevision hardwareRevision) {
        vfoAmp.setSelected(false);
        vfoAmp.setDisable(!hardwareRevision.isAmplifier());
    }

    void shutdown() {
        measurementsController.shutdown();
    }

    void requestDeviceState() {
        if(!radio3.isConnected()) return;
        
        Response<DeviceState> deviceStateResponse = radio3.readDeviceState();
        if(deviceStateResponse.isOK()) {
            DeviceState ds = deviceStateResponse.getData();
            updateDeviceProperties(ds);
            updateVfoOut(ds.vfoOut);
            updateVfoAmp(ds.vfoAmp);
            updateVfoAtt(ds.vfoAtt0, ds.vfoAtt1, ds.vfoAtt2);
        }
    }

    private void requestDeviceInfo() {
        Response<DeviceInfo> deviceInfoResponse = radio3.readDeviceInfo();
        if(deviceInfoResponse.isOK()) {
            updateDeviceProperties(deviceInfoResponse.getData());
        }
    }

    private void onRefresh(ActionEvent event) {
        devicePropertiesRefresh.setDisable(true);
        requestDeviceInfo();
        requestDeviceState();
        devicePropertiesRefresh.setDisable(false);
    }

    private void updateDeviceProperties() {
        deviceProperties.setAll(devicePropertiesMap.entrySet().stream().map(e -> new Property(e.getKey(), e.getValue())).collect(Collectors.toList()));
    }

    private void updateDeviceProperties(DeviceInfo di) {
        devicePropertiesMap.put(ui.text("device.prop.hardware"), di.name+" ("+di.hardwareRevision+")");
        devicePropertiesMap.put(ui.text("device.prop.firmware"), di.buildId);
        devicePropertiesMap.put(ui.text("device.prop.vfoType"), di.vfoType.toString());
        updateDeviceProperties();
    }

    private String formatOnOff(boolean on) {
        return ui.text(on ? "text.on" : "text.off");
    }

    private void updateDeviceProperties(DeviceState ds) {
        devicePropertiesMap.put(ui.text("device.prop.uptime"), ds.timeMs + " ms");
        devicePropertiesMap.put(ui.text("device.prop.vfoOut"), ds.vfoOut.toString());
        devicePropertiesMap.put(ui.text("device.prop.amplifier"), formatOnOff(ds.vfoAmp == VfoAmp.ON));
        devicePropertiesMap.put(ui.text("device.prop.attStage0"), formatOnOff(ds.vfoAtt0));
        devicePropertiesMap.put(ui.text("device.prop.attStage1"), formatOnOff(ds.vfoAtt1));
        devicePropertiesMap.put(ui.text("device.prop.attStage2"), formatOnOff(ds.vfoAtt2));
        updateDeviceProperties();
    }

    private void updateVfoAtt(boolean att0, boolean att1, boolean att2) {
        vfoAtt0.setSelected(att0);
        vfoAtt1.setSelected(att1);
        vfoAtt2.setSelected(att2);
    }

    void disableAllExcept(boolean flag, Object element) {
        FxUtils.setDisabled(flag, nonModalNodes.stream().filter(e -> e!=element).toArray());
    }

    void disableVfoOut(boolean flag) {
        FxUtils.setDisabled(flag, vfoOutDirect, vfoOutVna);
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