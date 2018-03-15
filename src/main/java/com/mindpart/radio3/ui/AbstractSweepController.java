package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.radio3.device.SweepResponse;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

import java.util.function.Consumer;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.28
 */
public abstract class AbstractSweepController {
    protected final Radio3 radio3;
    protected final UserInterface ui;
    protected final SweepSettingsController sweepSettingsController;

    @FXML
    protected Button btnOnce;

    @FXML
    protected ToggleButton btnContinuous;

    protected abstract void onSweepOnce();
    protected abstract void disableUI();
    protected abstract void enableUI();
    protected abstract void updateAnalyserData(SweepResponse ad);
    protected abstract Response<SweepResponse> sweepOnce();
    protected abstract void sweepSettingsChangeListener();

    public AbstractSweepController(Radio3 radio3, UserInterface ui) {
        this.radio3 = radio3;
        this.ui = ui;
        this.sweepSettingsController = new SweepSettingsController(ui, radio3.getSweepProfiles());
    }

    protected void initialize() {
        btnOnce.setOnAction(ev -> onSweepOnce());
        btnContinuous.selectedProperty().addListener(this::continuousChangeListener);
        sweepSettingsController.setRangeChangeListener(this::sweepSettingsChangeListener);
        sweepSettingsController.setQualityChangeListener(this::sweepSettingsChangeListener);
    }

    protected void runSweepOnce(Consumer<SweepResponse> analyserDataConsumer) {
        radio3.executeInBackground(() -> {
            if(radio3.isConnected()) {
                Response<SweepResponse> response = sweepOnce();
                if(response.isOK()) {
                    Platform.runLater(() -> analyserDataConsumer.accept(response.getData()));
                }
            } else {
                enableUI();
            }
        });
    }

    protected void continuousChangeListener(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean continuous) {
        if(continuous) {
            disableUI();
            runSweepOnce(this::displayDataAndSweepAgain);
        } else {
            enableUI();
        }
    }

    private void displayDataAndSweepAgain(SweepResponse analyserResponse) {
        if(btnContinuous.isSelected()) {
            updateAnalyserData(analyserResponse);
            runSweepOnce(this::displayDataAndSweepAgain);
        }
    }
}
