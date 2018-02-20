package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceConfiguration;
import com.mindpart.radio3.device.DeviceState;
import com.mindpart.radio3.device.LicenseData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Robert Jaremczak
 * Date: 2018.02.20
 */
public class ManifestController {
    private final UserInterface ui;
    private final Runnable onRefresh;

    @FXML
    Label licenseProduct;

    @FXML
    Label licenseOwner;

    @FXML
    Label licenseHardware;

    @FXML
    Label configHardware;

    @FXML
    Label configFirmware;

    @FXML
    Label configVfoType;

    @FXML
    Label stateVfoOut;

    @FXML
    Label stateAmplifier;

    @FXML
    Label stateAttenuator;

    @FXML
    Label stateRuntime;

    @FXML
    Button refreshButton;

    public ManifestController(UserInterface ui, Runnable onRefresh) {
        this.ui = ui;
        this.onRefresh = onRefresh;
    }

    public void initialize() {
        refreshButton.setOnAction(ae -> onRefresh.run());
    }

    private final String formatUniqueId(long id0, long id1, long id2) {
        return String.format("%08X-%08X-%08X", id0, id1, id2);
    }

    private String formatOnOff(boolean on) {
        return ui.text(on ? "text.on" : "text.off");
    }

    private String formatAttenuation(boolean att6dB, boolean att10dB, boolean att20dB) {
        return Integer.toString((att6dB ? -6 : 0) + (att10dB ? -10 : 0) + (att20dB ? -20 : 0));
    }

    public void update(LicenseData data) {
        licenseProduct.setText(data.product);
        licenseOwner.setText(data.owner);
        licenseHardware.setText(formatUniqueId(data.uniqueId0, data.uniqueId1, data.uniqueId2));
    }

    public void update(DeviceConfiguration dc) {
        configHardware.setText(formatUniqueId(dc.coreUniqueId0, dc.coreUniqueId1, dc.coreUniqueId2) + " (rev. " + dc.hardwareRevision + ")");
        configFirmware.setText(String.format("%02d.%02d-%s",
                dc.firmwareVersionMajor, dc.firmwareVersionMinor,
                ui.timestamp.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(dc.firmwareBuildTimestamp), ZoneId.systemDefault()))));
        configVfoType.setText(dc.vfoType.toString());
    }

    public void update(DeviceState ds) {
        stateVfoOut.setText(ds.vfoToVna ? "VNA" : "VFO");
        stateAmplifier.setText(formatOnOff(ds.amplifier));
        stateAttenuator.setText(formatAttenuation(ds.att6dB, ds.att10dB, ds.att20dB)+" dB");
        stateRuntime.setText(ds.timeMs + " ms");
    }
}
