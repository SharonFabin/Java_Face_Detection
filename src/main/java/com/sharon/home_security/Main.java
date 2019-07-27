package com.sharon.home_security;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        System.out.println(
                Main.class.getClassLoader().getResource(
                        "haarcascade_frontalface_alt.xml"));
        FXMLLoader loader =
                new FXMLLoader(Main.class.getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Laser Blinder");
        Scene scene = new Scene(root,1000,700);
        scene.getStylesheets().add("main.css");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

