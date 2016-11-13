package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.types.Frequency;
import com.mindpart.ui.FrequencyField;
import com.mindpart.utils.FxUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.31
 */
public class SweepSettings extends GridPane {

    @FXML
    FrequencyField startFrequencyField;

    @FXML
    FrequencyField endFrequencyField;

    @FXML
    TextField stepsField;

    @FXML
    ChoiceBox<SweepProfile> presetsChoiceBox;

    private ObservableList<SweepProfile> presets = FXCollections.observableArrayList();

    public SweepSettings(List<SweepProfile> presets) {
        this.presets.addAll(presets);
        FxUtils.loadFxml(this, "sweepConfigControl.fxml");
    }

    public void initialize() {
        startFrequencyField.setMaxSupplier(() -> endFrequencyField.getFrequency());
        endFrequencyField.setMinSupplier(() -> startFrequencyField.getFrequency());
        endFrequencyField.setMaxSupplier(() -> Frequency.ofMHz(70));

        presetsChoiceBox.setItems(presets);
        presetsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null) {
                startFrequencyField.clearOnChangeHandler();
                endFrequencyField.clearOnChangeHandler();

                startFrequencyField.setFrequency(Frequency.parse(newValue.freqMin));
                endFrequencyField.setFrequency(Frequency.parse(newValue.freqMax));
                stepsField.setText(Integer.toString(newValue.steps));

                startFrequencyField.setOnChangeHandler(() -> presetsChoiceBox.getSelectionModel().clearSelection());
                endFrequencyField.setOnChangeHandler(() -> presetsChoiceBox.getSelectionModel().clearSelection());
            }
        });
        presetsChoiceBox.getSelectionModel().selectFirst();
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
        return NumberUtils.toInt(stepsField.getText(), 100);
    }
}
