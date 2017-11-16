package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.SweepProfiles;
import com.mindpart.type.Frequency;
import com.mindpart.ui.DoubleField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.apache.log4j.Logger;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.31
 */
public class SweepSettingsController {
    private static Logger logger = Logger.getLogger(SweepSettingsController.class);
    private static final Frequency FREQUENCY_MIN = Frequency.ofMHz(0.1);
    private static final Frequency FREQUENCY_MAX = Frequency.ofMHz(70);
    private static final Frequency FREQUENCY_STEP = Frequency.ofMHz(0.01);
    private static final String FREQUENCY_FORMAT = "##0.000";

    @FXML
    DoubleField startFrequencyField;

    @FXML
    DoubleField endFrequencyField;

    @FXML
    ChoiceBox<SweepQuality> sweepQuality;

    @FXML
    ChoiceBox<SweepProfile> presetsChoiceBox;

    private BundleData bundleData;
    private ObservableList<SweepProfile> presets = FXCollections.observableArrayList();
    private Runnable rangeChangeListener = () -> {};
    private Runnable qualityChangeListener = () -> {};

    public SweepSettingsController(BundleData bundleData, SweepProfiles sweepProfiles) {
        this.bundleData = bundleData;
        this.presets.addAll(sweepProfiles.profiles);
    }

    private void initFrequencyField(DoubleField doubleField, double initValue, double step) {
        doubleField.setDecimalFormat(FREQUENCY_FORMAT);
        doubleField.setPrefColumnCount(6);
        doubleField.getDoubleValueFactory().setValue(initValue);
        doubleField.getDoubleValueFactory().setAmountToStepBy(step);
    }

    public void initialize() {
        sweepQuality.setConverter(bundleData.getGenericStringConverter());
        sweepQuality.getItems().addAll(SweepQuality.values());
        sweepQuality.getSelectionModel().select(SweepQuality.FAST);

        initFrequencyField(startFrequencyField, FREQUENCY_MIN.toMHz(), FREQUENCY_STEP.toMHz());
        initFrequencyField(endFrequencyField, FREQUENCY_MAX.toMHz(), FREQUENCY_STEP.toMHz());
        
        startFrequencyField.getDoubleValueFactory().setMin(FREQUENCY_MIN.toMHz());
        endFrequencyField.getDoubleValueFactory().setMax(FREQUENCY_MAX.toMHz());

        startFrequencyField.getDoubleValueFactory().maxProperty().bind(
                DoubleProperty.doubleProperty(endFrequencyField.getDoubleValueFactory().valueProperty()).subtract(FREQUENCY_STEP.toMHz()));

        endFrequencyField.getDoubleValueFactory().minProperty().bind(
                DoubleProperty.doubleProperty(startFrequencyField.getDoubleValueFactory().valueProperty()).add(FREQUENCY_STEP.toMHz()));

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
                startFrequencyField.getDoubleValueFactory().setValue(Frequency.parse(newProfile.freqMin).toMHz());
                endFrequencyField.getDoubleValueFactory().setValue(Frequency.parse(newProfile.freqMax).toMHz());
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
        return Frequency.ofMHz(startFrequencyField.getDoubleValueFactory().getValue());
    }

    public Frequency getEndFrequency() {
        return Frequency.ofMHz(endFrequencyField.getDoubleValueFactory().getValue());
    }

    public void setFrequencyRange(Frequency startFreq, Frequency endFreq) {
        if(!startFrequencyField.isDisabled() && !endFrequencyField.isDisabled()) {
            clearPreset();
            startFrequencyField.getDoubleValueFactory().setValue(startFreq.toMHz());
            endFrequencyField.getDoubleValueFactory().setValue(endFreq.toMHz());
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
