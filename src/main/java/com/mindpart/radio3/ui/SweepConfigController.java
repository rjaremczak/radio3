package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.types.Frequency;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2016.10.31
 */
public class SweepConfigController {

    @FXML
    TextField startFrequencyField;

    @FXML
    TextField endFrequencyField;

    @FXML
    TextField numStepsField;

    @FXML
    ChoiceBox<SweepProfile> presetsChoiceBox;

    private ObservableList<SweepProfile> presets = FXCollections.observableArrayList();
    private Frequency startFrequency;
    private Frequency endFrequency;
    private int steps = 500;

    public SweepConfigController(List<SweepProfile> presets) {
        this.presets.addAll(presets);
    }

    @FXML
    public void initialize() {
        presetsChoiceBox.setItems(presets);
        presetsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            startFrequency = Frequency.fromHz(newValue.freqMin);
            endFrequency = Frequency.fromHz(newValue.freqMax);
            steps = newValue.steps;
        });
    }

    public void onStartFrequency(ActionEvent actionEvent) {

    }

    public void onEndFrequency(ActionEvent actionEvent) {

    }

    public void onNumSteps(ActionEvent actionEvent) {

    }

    public Frequency getStartFrequency() {
        return startFrequency;
    }

    public Frequency getEndFrequency() {
        return endFrequency;
    }

    public int getSteps() {
        return steps;
    }
}
