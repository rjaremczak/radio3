package com.mindpart.radio3.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public abstract class ProbeController extends FeatureController {
    protected ToggleButton runButton;

    protected void setUp(String title, String name, String unit) {
        setUp(title, name, false, unit, "Get");
        runButton = new ToggleButton("Continuous");
        runButton.setSelected(false);
        runButton.setOnAction(this::onContinuous);
        buttonBox.getChildren().add(runButton);
    }

    protected void onContinuous(ActionEvent event) {
        mainButton.setDisable(runButton.isSelected());
    }
}
