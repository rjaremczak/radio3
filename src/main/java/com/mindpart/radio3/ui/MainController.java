package com.mindpart.radio3.ui;

import com.mindpart.radio3.Radio3;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Robert Jaremczak
 * Date: 2016.02.04
 */
public class MainController implements Initializable {
    private Radio3 radio3;

    @FXML public ChoiceBox<String> deviceSelection;
    @FXML public Button deviceConnect;
    @FXML public Label deviceStatus;

    public MainController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    public void initDeviceSelection(ActionEvent actionEvent) {
        deviceSelection.setItems(FXCollections.observableList(radio3.getDeviceService().availableSerialPorts()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDeviceSelection(null);
    }
}
