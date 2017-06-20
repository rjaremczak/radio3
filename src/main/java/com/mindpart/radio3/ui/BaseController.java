package com.mindpart.radio3.ui;

import com.mindpart.ui.FxUtils;
import javafx.scene.Parent;

import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.20
 */
public class BaseController {
    private static ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle("bundle");
    }

    public Parent loadFXml(String fxml) {
        return FxUtils.loadFXml(this, fxml, bundle);
    }

}
