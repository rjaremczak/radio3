package com.mindpart.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;

import java.io.IOException;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.03
 */
public final class FxUtils {
    private FxUtils() {}

    public static void loadFxml(Object root, String resourceName) {
        FXMLLoader loader = new FXMLLoader(root.getClass().getResource(resourceName));
        loader.setRoot(root);
        loader.setControllerFactory(clazz -> root);
        try {
            loader.load();
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

}
