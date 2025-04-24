package com.example.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginController {

    private ClientMainModel client;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField loginField;

    @FXML
    private Button loginButton;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[a-z]+$";


    //Esegue il login anche tramite la pressione del tasto "invio"
    @FXML
    public void initialize() {
        loginField.setOnAction(event -> {
            try {
                onLoginButtonClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {

        if (client == null) client = new ClientMainModel();

        if (loginField.getText().isEmpty()) {
            errorLabel.setText("Inserire un indirizzo email");
        } else if (!Pattern.compile(EMAIL_REGEX).matcher(loginField.getText()).matches()) {
            errorLabel.setText("Email non ben formattata");
        } else {

            int checkUser = client.checkUser(loginField.getText());

            if (checkUser == 0) {

                //imposta il nome dello user nell'istanza del client
                client.setUser(loginField.getText());

                //apre la main-view e passa l'istanza client di ClientMainModel
                FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("main-view.fxml"));
                Parent root = fxmlLoader.load();
                MainController mainController = fxmlLoader.getController();
                mainController.setClient(client);
                Scene scene = new Scene(root, 970, 650);
                Stage stage = (Stage) loginField.getScene().getWindow();
                // aggiungere poi il passaggio del modello
                stage.setTitle("Main <" + client.getUser() + ">");
                stage.setScene(scene);
                stage.show();
                stage.centerOnScreen(); //apre la finestra al centro dello schermo

            } else {
                String message = "Messaggio di errore non definito";

                switch (checkUser) {
                    case 1:
                        message = "JSON non valido";
                        break;
                    case 2:
                        message = "Comando non valido";
                        break;
                    case 3:
                        message = "Utente non trovato";
                        break;
                    case 4:
                        message = "Formato Email non valido";
                        break;
                    case 5:
                        message = "Utente non trovato";
                        break;
                    case 6:
                        message = "Errore FileSystem";
                        break;
                    case 7:
                        message = "Email non presente";
                        break;
                    case 8:
                        message = "Destinatario non corretto";
                        break;
                    case 9:
                        message = "Non tutte le mail inviate";
                        break;
                    case 10:
                        message = "Email non trovata";
                        break;
                    case 999:
                        message = "Errore di connessione al server";
                        break;
                }

                errorLabel.setText(message);
            }
        }
    }
}