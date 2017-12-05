package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.SweepProfiles;
import com.mindpart.science.Frequency;
import com.mindpart.science.UnitPrefix;
import com.mindpart.ui.DoubleSpinner;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.apache.log4j.Logger;

import static com.mindpart.science.UnitPrefix.MEGA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.31
 */
public class SweepSettingsController {
    private static Logger logger = Logger.getLogger(SweepSettingsController.class);

    @FXML
    DoubleSpinner startFrequencyField;

    @FXML
    DoubleSpinner endFrequencyField;

    @FXML
    ChoiceBox<SweepQuality> sweepQuality;

    @FXML
    ChoiceBox<SweepProfile> presetsChoiceBox;

    private UserInterface ui;
    private ObservableList<SweepProfile> presets = FXCollections.observableArrayList();
    private Runnable rangeChangeListener = () -> {};
    private Runnable qualityChangeListener = () -> {};

    public SweepSettingsController(UserInterface ui, SweepProfiles sweepProfiles) {
        this.ui = ui;
        this.presets.addAll(sweepProfiles.profiles);
    }

    public void initialize() {
        sweepQuality.setConverter(ui.getGenericStringConverter());
        sweepQuality.getItems().addAll(SweepQuality.values());
        sweepQuality.getSelectionModel().select(SweepQuality.FAST);

        ui.initFrequencyField(startFrequencyField);
        ui.initFrequencyField(endFrequencyField);

        startFrequencyField.getDoubleValueFactory().maxProperty().bind(
                DoubleProperty.doubleProperty(endFrequencyField.getDoubleValueFactory().valueProperty())
                        .subtract(startFrequencyField.getDoubleValueFactory().getAmountToStepBy()));

        endFrequencyField.getDoubleValueFactory().minProperty().bind(
                DoubleProperty.doubleProperty(startFrequencyField.getDoubleValueFactory().valueProperty())
                        .add(startFrequencyField.getDoubleValueFactory().getAmountToStepBy()));

        startFrequencyField.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearPreset();
            rangeChangeListener.run();
        });

        endFrequencyField.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearPreset();
            rangeChangeListener.run();
        });

        presetsChoiceBox.setItems(presets);
        presetsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newProfile) -> {
            if(newProfile!=null) {
                startFrequencyField.getDoubleValueFactory().setValue(newProfile.freqMin);
                endFrequencyField.getDoubleValueFactory().setValue(newProfile.freqMax);
                rangeChangeListener.run();
            }
        });
        presetsChoiceBox.getSelectionModel().selectFirst();
        sweepQuality.getSelectionModel().selectedItemProperty().addListener(this::onSweepQualityChange);
    }

    private void onSweepQualityChange(ObservableValue<? extends SweepQuality> ob, SweepQuality old, SweepQuality current) {
        qualityChangeListener.run();
    }

    private void clearPreset() {
        presetsChoiceBox.getSelectionModel().clearSelection();
    }

    public Frequency getStartFrequency() {
        return new Frequency(startFrequencyField.getDoubleValueFactory().getValue(), MEGA);
    }

    public Frequency getEndFrequency() {
        return new Frequency(endFrequencyField.getDoubleValueFactory().getValue(), MEGA);
    }

    public void setFrequencyRange(double startFreqMHz, double endFreqMHz) {
        if(!startFrequencyField.isDisabled() && !endFrequencyField.isDisabled()) {
            clearPreset();
            startFrequencyField.getDoubleValueFactory().setValue(startFreqMHz);
            endFrequencyField.getDoubleValueFactory().setValue(endFreqMHz);
        }
    }

    public SweepQuality getQuality() {
        return sweepQuality.getSelectionModel().getSelectedItem();
    }

    public void disableControls(boolean disable) {
        startFrequencyField.setDisable(disable);
        endFrequencyField.setDisable(disable);
        sweepQuality.setDisable(disable);
        presetsChoiceBox.setDisable(disable);
    }

    public void setRangeChangeListener(Runnable listener) {
        this.rangeChangeListener = listener;
    }

    public void setQualityChangeListener(Runnable listener) {
        this.qualityChangeListener = listener;
    }
}
