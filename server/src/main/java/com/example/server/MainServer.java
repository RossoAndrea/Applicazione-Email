package com.example.server;


import com.example.server.server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainServer extends Application {

    private static MainServerModel model;

    private static Server server;

    public static void main(String[] args) throws IOException {
        System.out.println("Main server started");

        model = new MainServerModel();

        server = new Server();
        server.start();

        model.getObservables().newList();
        log("Main server started");

        launch();
    }

    /**
     * Starts the main server, loading the FXML file and setting the scene and passing the model to the controller
     * @param stage the stage
     * @throws Exception if an error occurs
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainServer.class.getResource("MainServerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        MainServerController mainServerController = fxmlLoader.getController();
        mainServerController.initModel(model);

        stage.setTitle("Main Server");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainServer.class.getResourceAsStream("icons8-server-50.png"))));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        (server).interrupt();
        System.out.println("Server is closing");
    }


    /**
     * Logs a message to the console and the list view
     * @param message the message to log
     */
    public static void log(String message) {
        System.out.println(message);
        model.getObservables().addListItem(message);
    }

    /**
     * Returns the model
     * @return the model
     */
    public static MainServerModel getModel() {
        return model;
    }

}
