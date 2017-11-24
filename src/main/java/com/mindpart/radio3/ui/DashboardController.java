package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Probes;
import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.ui.DoubleSpinner;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mindpart.type.UnitPrefix.MEGA;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.20
 */
public class DashboardController {
    private static final Logger logger = Logger.getLogger(DashboardController.class);

    private final Radio3 radio3;
    private final UserInterface ui;

    private final ScheduledExecutorService continuousSampling = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean continuousSamplingEnabled = new AtomicBoolean(false);

    public DashboardController(Radio3 radio3, UserInterface ui) {
        this.radio3 = radio3;
        this.ui = ui;
    }

    @FXML
    protected DoubleSpinner vfoFreq;

    @FXML
    protected Label fMeterFreq;

    @FXML
    protected Label logProbeValue;

    @FXML
    protected Label linProbeValue;

    @FXML
    protected Label vnaProbeValue;

    public void initialize() {
        continuousSampling.scheduleWithFixedDelay(() -> {
            if(continuousSamplingEnabled.get()) { Platform.runLater(this::sampleAllProbes); }
        }, 200, 200, TimeUnit.MILLISECONDS);

        ui.initFrequencyFieldWithRanges(vfoFreq);
        vfoFreq.getDoubleValueFactory().valueProperty().addListener((observable, oldValue, newFreq) -> radio3.writeVfoFrequency((int) MEGA.toBase(newFreq)));
    }

    private void sampleAllProbes() {
        Response<Probes> response = radio3.readAllProbes();
        if(response.isOK()) {
            Probes probes = response.getData();
            fMeterFreq.setText(ui.frequency.format(probes.getFMeter()));
            logProbeValue.setText(ui.frequency.format(probes.getLogarithmic()));
            linProbeValue.setText(ui.frequency.format(probes.getLinear()));
            vnaProbeValue.setText(ui.impedance.format(probes.getVnaResult().getImpedance()));
        }
    }

    void activate() {
        logger.debug("activate");
        requestVfoFrequency();
        continuousSamplingEnabled.set(true);
    }

    void deactivate() {
        logger.debug("deactivate");
        continuousSamplingEnabled.set(false);
    }

    void shutdown() {
        deactivate();
        continuousSampling.shutdownNow();
    }

    private void requestVfoFrequency() {
        Response<Integer> response = radio3.readVfoFrequency();
        if(response.isOK()) {
            vfoFreq.getDoubleValueFactory().setValue(MEGA.fromBase(response.getData()));
        }
    }
}
