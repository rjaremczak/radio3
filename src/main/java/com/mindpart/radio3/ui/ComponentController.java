package com.mindpart.radio3.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public abstract class ComponentController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }

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

    abstract protected void initialize();

    abstract public void onMainButton(ActionEvent actionEvent);
}