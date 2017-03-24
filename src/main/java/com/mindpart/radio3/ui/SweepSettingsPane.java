package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.types.Frequency;
import com.mindpart.ui.FrequencyField;
import com.mindpart.utils.FxUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.31
 */
public class SweepSettingsPane extends GridPane {
    private static Logger logger = Logger.getLogger(SweepSettingsPane.class);

    @FXML
    FrequencyField startFrequencyField;

    @FXML
    FrequencyField endFrequencyField;

    @FXML
    ChoiceBox<SweepQuality> sweepQuality;

    @FXML
    ChoiceBox<SweepProfile> presetsChoiceBox;

    private ObservableList<SweepProfile> presets = FXCollections.observableArrayList();
    private Runnable rangeChangeListener = () -> {};
    private Runnable qualityChangeListener = () -> {};

    public SweepSettingsPane(List<SweepProfile> presets) {
        this.presets.addAll(presets);
        FxUtils.loadFxml(this, "sweepSettingsPane.fxml");
    }

    private void initSweepSteps() {
        sweepQuality.getItems().addAll(SweepQuality.values());
        sweepQuality.getSelectionModel().select(SweepQuality.BALANCED);
    }

    public void initialize() {
        initSweepSteps();

        startFrequencyField.setMaxSupplier(() -> endFrequencyField.getFrequency());
        startFrequencyField.setChangeListener(this::internalRangeChangeListener);

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

    void internalRangeChangeListener() {
        presetsChoiceBox.getSelectionModel().clearSelection();
        rangeChangeListener.run();
    }

    public Frequency getStartFrequency() {
        return startFrequencyField.getFrequency();
    }

    public void setStartFrequency(Frequency frequency) {
        if(!startFrequencyField.isDisabled()) { startFrequencyField.setFrequency(frequency); }
    }

    public Frequency getEndFrequency() {
        return endFrequencyField.getFrequency();
    }

    public void setEndFrequency(Frequency frequency) {
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
