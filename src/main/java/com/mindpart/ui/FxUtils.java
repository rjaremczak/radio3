package com.mindpart.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.03
 */
public final class FxUtils {
    private FxUtils() {}

    public static Parent loadFXml(Object controller, String fxml, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(fxml), resources);
        loader.setControllerFactory(clazz -> controller);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void alert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void alertInputError(String label, String message, String details) {
        alert(Alert.AlertType.ERROR, label, message, details);
    }

    public static double valueFromSeries(XYChart.Series<Number, Number> series, double argument) {
        Number number = null;
        for(XYChart.Data<Number, Number> item : series.getData()) {
            if(item.getXValue().doubleValue() > argument) { break; }
            number = item.getYValue();
        }
        return number!=null ? number.doubleValue() : 0;
    }

    public static void setDisabled(boolean flag, Object... items) {
        for (Object item : items) {
            if (item instanceof Node) {
                ((Node) item).setDisable(flag);
            } else if (item instanceof Tab) {
                ((Tab) item).setDisable(flag);
            }
        }
    }

    public static void disableItems(Object... items) {
        setDisabled(true, items);
    }

    public static void enableItems(Object... items) {
        FxUtils.setDisabled(false, items);
    }
}
