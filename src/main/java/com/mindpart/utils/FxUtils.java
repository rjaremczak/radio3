package com.mindpart.utils;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.03
 */
public final class FxUtils {
    private FxUtils() {
    }

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
}
