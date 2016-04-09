package com.mindpart.radio3.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public abstract class ProbeController extends FeatureController {
    private Runnable startSampling;
    private Runnable stopSampling;

    protected ToggleButton runButton;

    protected void setUp(String title, String name, String unit, Runnable startSampling, Runnable stopSampling) {
        setUp(title, name, false, unit, "Get");
        this.startSampling = startSampling;
        this.stopSampling = stopSampling;

        runButton = new ToggleButton("Continuous");
        runButton.setSelected(false);
        runButton.setOnAction(this::onContinuous);
        buttonBox.getChildren().add(runButton);
    }

    protected void onContinuous(ActionEvent event) {
        if(runButton.isSelected()) {
            mainButton.setDisable(true);
            startSampling.run();

        } else {
            stopSampling.run();
            mainButton.setDisable(false);
        }
    }
}
