package com.mindpart.radio3.ui;

import com.mindpart.radio3.Radio3;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Radio3 radio3;
    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        radio3 = new Radio3();
        mainController = new MainController(radio3);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        loader.setControllerFactory(clazz -> mainController);
        Parent root = loader.load();
        primaryStage.setTitle("Radio 3");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
