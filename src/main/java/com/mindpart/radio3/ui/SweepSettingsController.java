package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.SweepProfiles;
import com.mindpart.types.Frequency;
import com.mindpart.ui.FrequencyField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.31
 */
public class SweepSettingsController {
    private static Logger logger = Logger.getLogger(SweepSettingsController.class);

    @FXML
    FrequencyField startFrequencyField;

    @FXML
    FrequencyField endFrequencyField;

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

    private void initSweepSteps() {
        sweepQuality.setConverter(new StringConverter<SweepQuality>() {
            @Override
            public String toString(SweepQuality object) {
                String str = object.toString();
                return str.startsWith("%") ? bundleData.resolve(str.substring(1)) : str;
            }

            @Override
            public SweepQuality fromString(String string) {
                return null;
            }
        });
        sweepQuality.getItems().addAll(SweepQuality.values());
        sweepQuality.getSelectionModel().select(SweepQuality.BEST);
    }

    public void initialize() {
        initSweepSteps();

        startFrequencyField.initFromBundle(bundleData);
        startFrequencyField.setMinSupplier(() -> Frequency.ofMHz(0.1));
        startFrequencyField.setMaxSupplier(() -> endFrequencyField.getFrequency());
        startFrequencyField.setChangeListener(this::internalRangeChangeListener);

        endFrequencyField.initFromBundle(bundleData);
        endFrequencyField.setMinSupplier(() -> startFrequencyField.getFrequency());
        endFrequencyField.setMaxSupplier(() -> Frequency.ofMHz(150));
        endFrequencyField.setChangeListener(this::internalRangeChangeListener);

        presetsChoiceBox.setItems(presets);
        presetsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newProfile) -> {
            if(newProfile!=null) {
                startFrequencyField.setFrequency(Frequency.parse(newProfile.freqMin));
                endFrequencyField.setFrequency(Frequency.parse(newProfile.freqMax));
                rangeChangeListener.run();
            }
        });
        presetsChoiceBox.getSelectionModel().selectFirst();
        sweepQuality.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> qualityChangeListener.run());
    }

    private void clearPreset() {
        presetsChoiceBox.getSelectionModel().clearSelection();
    }

    void internalRangeChangeListener() {
        clearPreset();
        rangeChangeListener.run();
    }

    public Frequency getStartFrequency() {
        return startFrequencyField.getFrequency();
    }

    public void setStartFrequency(Frequency frequency) {
        clearPreset();
        if(!startFrequencyField.isDisabled()) { startFrequencyField.setFrequency(frequency); }
    }

    public Frequency getEndFrequency() {
        return endFrequencyField.getFrequency();
    }

    public void setEndFrequency(Frequency frequency) {
        clearPreset();
        if(!endFrequencyField.isDisabled()) { endFrequencyField.setFrequency(frequency); }
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
