package com.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainClient extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 160); //dimensioni per login
        stage.setTitle("MainClient");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainClient.class.getResourceAsStream("img_1.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            launch();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}