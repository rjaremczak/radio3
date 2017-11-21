package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.ProbesValues;
import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.ui.DoubleSpinner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.20
 */
public class DashboardController {
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
    protected Button vfoFreqSet;

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


    }

    public void sampleAllProbes() {
        Response<ProbesValues> response = radio3.readAllProbes();
        if(response.isOK()) {
            //updateAllProbes(response.getData());
        }
    }

    public void activate() {
        requestVfoFrequency();
        continuousSamplingEnabled.set(true);
    }

    public void deactivate() {
        continuousSamplingEnabled.set(false);
    }

    public void shutdown() {
        deactivate();
        continuousSampling.shutdownNow();
    }

    private void requestVfoFrequency() {
        Response<Integer> response = radio3.readVfoFrequency();
        if(response.isOK()) {
            //vfoController.update(response.getData());
        }
    }
}
