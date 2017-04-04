package com.mindpart.radio3.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public abstract class ComponentController {

    @FXML
    protected Pane pane;

    @FXML
    protected Label probeName;

    @FXML
    protected Label valueName;

    @FXML
    protected TextField valueField;

    @FXML
    protected HBox mainBox;

    @FXML
    protected Button mainButton;

    @FXML
    abstract protected void initialize();

    protected void setUpAsProbe(String title, String name) {
        setUp(title, name, false, "Get");
    }

    protected void setUp(String title, String name, boolean editable, String mainButtonText) {
        probeName.setText(title);
        valueName.setText(name);
        valueField.setText("");
        valueField.setEditable(editable);
        mainButton.setText(mainButtonText);
    }

    public void setDisable(boolean disable) {
        pane.setDisable(disable);
    }

    public void disableMainButton(boolean disable) {
        mainButton.setDisable(disable);
    }

    public void setValue(String str) {
        valueField.setText(str);
    }

    public String getValue() {
        return valueField.getText();
    }

    abstract public void onMainButton(ActionEvent actionEvent);
}