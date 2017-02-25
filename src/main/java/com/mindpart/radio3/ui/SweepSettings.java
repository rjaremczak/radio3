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
public class SweepSettings extends GridPane {
    private static Logger logger = Logger.getLogger(SweepSettings.class);

    @FXML
    FrequencyField startFrequencyField;

    @FXML
    FrequencyField endFrequencyField;

    @FXML
    ChoiceBox<Integer> sweepSteps;

    @FXML
    ChoiceBox<SweepProfile> presetsChoiceBox;

    private ObservableList<SweepProfile> presets = FXCollections.observableArrayList();

    public SweepSettings(List<SweepProfile> presets) {
        this.presets.addAll(presets);
        FxUtils.loadFxml(this, "sweepConfigControl.fxml");
    }

    private void initSweepSteps() {
        sweepSteps.getItems().addAll(100, 200, 500, 1000);
    }

    public void initialize() {
        initSweepSteps();

        startFrequencyField.setMaxSupplier(() -> endFrequencyField.getFrequency());
        endFrequencyField.setMinSupplier(() -> startFrequencyField.getFrequency());
        endFrequencyField.setMaxSupplier(() -> Frequency.ofMHz(70));

        presetsChoiceBox.setItems(presets);
        presetsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newProfile) -> {
            if(newProfile!=null) {
                logger.debug("sweep profile selected: "+newProfile.dump());
                startFrequencyField.clearOnChangeHandler();
                endFrequencyField.clearOnChangeHandler();

                startFrequencyField.setFrequency(Frequency.parse(newProfile.freqMin));
                endFrequencyField.setFrequency(Frequency.parse(newProfile.freqMax));
                sweepSteps.getSelectionModel().select((Integer)newProfile.steps);

                startFrequencyField.setOnChangeHandler(() -> presetsChoiceBox.getSelectionModel().clearSelection());
                endFrequencyField.setOnChangeHandler(() -> presetsChoiceBox.getSelectionModel().clearSelection());
            }
        });
        presetsChoiceBox.getSelectionModel().selectFirst();
        if(sweepSteps.getSelectionModel().getSelectedItem()==null) {
            sweepSteps.getSelectionModel().selectFirst();
        }
    }

    public Frequency getStartFrequency() {
        return startFrequencyField.getFrequency();
    }

    public void setStartFrequency(Frequency frequency) {
        startFrequencyField.setFrequency(frequency);
    }

    public Frequency getEndFrequency() {
        return endFrequencyField.getFrequency();
    }

    public void setEndFrequency(Frequency frequency) {
        endFrequencyField.setFrequency(frequency);
    }

    public int getSteps() {
        return sweepSteps.getSelectionModel().getSelectedItem();
    }

    public void setEditable(boolean editable) {
        startFrequencyField.setEditable(editable);
        endFrequencyField.setEditable(editable);
        sweepSteps.setDisable(!editable);
        presetsChoiceBox.setDisable(!editable);
    }
}
