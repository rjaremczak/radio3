package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.*;
import com.mindpart.types.Frequency;
import com.mindpart.ui.FxUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

import java.io.IOException;
import java.util.*;
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
    ToggleButton outVfo;

    @FXML
    ToggleButton outVna;

    private Radio3 radio3;
    private ObservableList<String> availablePortNames = FXCollections.observableArrayList();
    private Map<String, String> devicePropertiesMap = new LinkedHashMap<>();
    private ObservableList<Property> deviceProperties = FXCollections.observableArrayList();
    private ScheduledExecutorService continuousSampling = Executors.newSingleThreadScheduledExecutor();
    private List<Object> nonModalNodes;

    private VnaController vnaController;
    private SweepController sweepController;
    private VfoController vfoController;
    private FreqMeterController freqMeterController;
    private LogarithmicProbeController logarithmicProbeController;
    private LinearProbeController linearProbeController;
    private VnaProbeController vnaProbeController;

    private volatile boolean continuousSamplingEnabled = false;

    public final BundleData bundle;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
        bundle = new BundleData(radio3.getConfiguration().getLocale());
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

    private <T extends ComponentController> void addFeatureBox(T controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("featureBox.fxml"));
        loader.setControllerFactory(clazz -> controller);
        componentsBox.getChildren().add(loader.load());
    }

    private void updateOnConnect() {
        btnConnect.setText(bundle.buttonDisconnect);
        FxUtils.enableItems(toolBar, btnConnect, componentsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
        FxUtils.disableItems(serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        updateDeviceStatus(DeviceStatus.READY);
    }

    private void updateOnDisconnect(DeviceStatus deviceStatus) {
        btnConnect.setText(bundle.buttonConnect);
        FxUtils.enableItems(btnConnect, serialPorts, serialPortsRefresh, hardwareRevisions, vfoType);
        FxUtils.disableItems(toolBar, componentsTab, sweepTab, vnaTab, deviceRuntimePane, deviceControlPane, configurationBox);
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


    public Parent loadFXml(Object controller, String fxml) {
        return FxUtils.loadFXml(controller, fxml, bundle.getResourceBundle());
    }

    public void initialize() throws IOException {
        nonModalNodes = Arrays.asList(deviceTab, sweepTab, vnaTab, componentsTab);

        devicePropertiesTable.setItems(deviceProperties);
        devicePropertiesRefresh.setOnAction(this::onRefresh);
        serialPortsRefresh.setOnAction((event) -> updateAvailablePorts());
        btnConnect.setOnAction(this::doConnectDisconnect);
        serialPorts.setItems(availablePortNames);

        vnaController = new VnaController(radio3, this);
        sweepController = new SweepController(radio3, this);
        sweepTab.setContent(loadFXml(sweepController, "sweepPane.fxml"));
        vnaTab.setContent(loadFXml(vnaController, "vnaPane.fxml"));

        vfoController = new VfoController(radio3);
        freqMeterController = new FreqMeterController(radio3);
        logarithmicProbeController = new LogarithmicProbeController(radio3);
        linearProbeController = new LinearProbeController(radio3);
        vnaProbeController = new VnaProbeController(radio3);

        addFeatureBox(vfoController);
        addFeatureBox(freqMeterController);
        addFeatureBox(logarithmicProbeController);
        addFeatureBox(linearProbeController);
        addFeatureBox(vnaProbeController);

        initVfoOut();
        initHardwareRevision();
        initVfoType();
        initVfoAmp();
        initVfoAtt();

        updateOnDisconnect(DeviceStatus.DISCONNECTED);
        updateAvailablePorts();

        continuousSampling.scheduleWithFixedDelay(() -> {
            if(continuousSamplingEnabled) { Platform.runLater(this::sampleAllProbes); }
        }, 200, 200, TimeUnit.MILLISECONDS);

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> tabPaneListener(newValue));
    }

    private void tabPaneListener(Tab selectedPane) {
        if(selectedPane == componentsTab) {
            disableVfoOut(false);
            requestVfoFrequency();
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
        FxUtils.setDisabled(hardwareRevision != HardwareRevision.VERSION_2, vfoAtt0, vfoAtt1, vfoAtt2);
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
                vfoOut.selectToggle(outVfo);
                break;
            }
            case VNA: {
                vfoOut.selectToggle(outVna);
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
        vfoAmp.setDisable(hardwareRevision != HardwareRevision.VERSION_2);
    }

    void shutdown() {
        continuousSampling.shutdown();
    }

    void requestDeviceState() {
        Response<DeviceState> deviceStateResponse = radio3.readDeviceState();
        if(deviceStateResponse.isOK()) {
            DeviceState ds = deviceStateResponse.getData();
            updateDeviceProperties(ds);
            updateVfoOut(ds.vfoOut);
            updateVfoAmp(ds.vfoAmp);
            updateVfoAtt(ds.vfoAtt0, ds.vfoAtt1, ds.vfoAtt2);
        }
    }

    private void requestVfoFrequency() {
        Response<Frequency> response = radio3.readVfoFrequency();
        if(response.isOK()) vfoController.update(response.getData());
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

    public void sampleAllProbes() {
        Response<ProbesValues> response = radio3.readAllProbes();
        if(response.isOK()) updateAllProbes(response.getData());
    }

    private void updateAllProbes(ProbesValues probesValues) {
        logarithmicProbeController.update(probesValues.getLogarithmic());
        linearProbeController.update(probesValues.getLinear());
        vnaProbeController.update(probesValues.getVnaResult());
        freqMeterController.update(probesValues.getFMeter());
    }

    private void disableGetOnAllProbes(boolean disable) {
        logarithmicProbeController.disableMainButton(disable);
        linearProbeController.disableMainButton(disable);
        vnaProbeController.disableMainButton(disable);
        freqMeterController.disableMainButton(disable);
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

    private void updateDeviceProperties(DeviceInfo di) {
        devicePropertiesMap.put("Hardware", di.name+" ("+di.hardwareRevision+")");
        devicePropertiesMap.put("Firmware", di.buildId);
        devicePropertiesMap.put("VFO", di.vfoType.toString());
        devicePropertiesMap.put("Baud rate", Long.toString(di.baudRate));
        updateDeviceProperties();
    }

    private void updateDeviceProperties(DeviceState ds) {
        devicePropertiesMap.put("Time since reset", ds.timeMs + " ms");
        devicePropertiesMap.put("VFO output", ds.vfoOut.toString());
        devicePropertiesMap.put("VFO amplifier", ds.vfoAmp.toString());
        devicePropertiesMap.put("VFO attenuator stage 0", Boolean.toString(ds.vfoAtt0));
        devicePropertiesMap.put("VFO attenuator stage 1", Boolean.toString(ds.vfoAtt1));
        devicePropertiesMap.put("VFO attenuator stage 2", Boolean.toString(ds.vfoAtt2));
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
        FxUtils.setDisabled(flag, outVfo, outVna);
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
        this.deviceStatus.setText(portName + deviceStatus.toString());
    }
}