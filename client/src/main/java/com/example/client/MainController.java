package com.example.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import com.example.client.model.Mail;
import javafx.util.Callback;

public class MainController implements Initializable {

    private ClientMainModel client;
    private StringProperty feedbackProperty;
    private Thread checkMailThread;
    private boolean running;
    private int lastMailCount;

    @FXML
    public Label errorLabel;

    @FXML
    private Label connectionStatus;

    @FXML
    private ListView<Mail> messageList;

    @FXML
    private TextField senderField;

    @FXML
    private TextField recipientsField;

    @FXML
    public TextField subjectField;

    @FXML
    private TextArea messageContent;

    @FXML
    private Button newMessageButton;

    @FXML
    private Button replyButton;

    @FXML
    private Button replyAllButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button deleteButton;

    public void setClient(ClientMainModel client) {

        this.client = client;

        getMail();
        Collections.sort(messageList.getItems());

        // Avvia il thread per il controllo delle nuove email
        loop();
    }

    @FXML
    protected void onNewMessageButtonClick() throws IOException {
        //apre CreateNewMessageController e passa l'istanza client di ClientMainModel
        FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("create_new_message-view.fxml"));
        Parent root = fxmlLoader.load();
        CreateNewMessageController createNewMessageController = fxmlLoader.getController();
        createNewMessageController.setClient(client);

        // Crea un nuovo Stage (nuova finestra)
        Stage newStage = new Stage();
        newStage.setTitle("Scrivi nuovo messaggio <" + client.getUser() + ">");
        newStage.setScene(new Scene(root, 550, 400));

        // Opzionale: Centra la nuova finestra
        newStage.centerOnScreen();

        // Mostra la nuova finestra senza chiudere quella attuale
        newStage.show();
    }

    @FXML
    protected void onReplyButtonClick() throws IOException {
        Mail selectedMail = messageList.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            try {
                if (selectedMail.getSender().equals("Server")) {
                    feedbackProperty.set("Non puoi rispondere a un messaggio del server");
                    return;
                }


                client.reply(selectedMail);
                
                // Apre la schermata di composizione con i campi precompilati
                FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("create_new_message-view.fxml"));
                Parent root = fxmlLoader.load();
                CreateNewMessageController createNewMessageController = fxmlLoader.getController();
                createNewMessageController.setClient(client);
                
                // Precompila i campi
                Mail replyMail = new Mail();
                replyMail.setRecipients(List.of(selectedMail.getSender()));
                replyMail.setSubject("Re: " + selectedMail.getSubject());
                replyMail.setBody("\n\n\n\n\n    Il " + selectedMail.getDateTimeFormatted() + ", " + selectedMail.getSender() + " ha scritto: \n\n" + selectedMail.getBody() + "\n");
                
                createNewMessageController.precompileFields(replyMail);

                // Crea un nuovo Stage (nuova finestra)
                Stage newStage = new Stage();
                newStage.setTitle("Scrivi nuovo messaggio <" + client.getUser() + ">");
                newStage.setScene(new Scene(root, 550, 400));

                // Opzionale: Centra la nuova finestra
                newStage.centerOnScreen();

                // Mostra la nuova finestra senza chiudere quella attuale
                newStage.show();
            } catch (IOException e) {
                feedbackProperty.set("Errore durante la risposta: " + e.getMessage());
            }
        } else {
            feedbackProperty.set("Seleziona un messaggio per rispondere");
        }
    }

    @FXML
    protected void onReplyAllButtonClick() throws IOException {
        Mail selectedMail = messageList.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            try {
                if (selectedMail.getSender().equals("Server")) {
                    feedbackProperty.set("Non puoi rispondere a un messaggio del server");
                    return;
                }
                client.replyAll(selectedMail);
                
                // Apre la schermata di composizione con i campi precompilati
                FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("create_new_message-view.fxml"));
                Parent root = fxmlLoader.load();
                CreateNewMessageController createNewMessageController = fxmlLoader.getController();
                createNewMessageController.setClient(client);
                
                // Precompila i campi
                Mail replyAllMail = new Mail();
                List<String> recipients = new ArrayList<>(selectedMail.getRecipients());
                // Aggiungi il mittente originale se non è l'utente corrente
                if (!selectedMail.getSender().equals(client.getUser())) {
                    recipients.add(selectedMail.getSender());
                }
                // Rimuovi l'utente corrente dai destinatari
                recipients.removeIf(recipient -> recipient.equals(client.getUser()));
                
                replyAllMail.setRecipients(recipients);
                replyAllMail.setSubject("Re: " + selectedMail.getSubject());
                replyAllMail.setBody("\n\n\n\n\n    Il " + selectedMail.getDateTimeFormatted() + ", " + selectedMail.getSender() + " ha scritto: \n\n" + selectedMail.getBody() + "\n");
                
                createNewMessageController.precompileFields(replyAllMail);

                // Crea un nuovo Stage (nuova finestra)
                Stage newStage = new Stage();
                newStage.setTitle("Scrivi nuovo messaggio <" + client.getUser() + ">");
                newStage.setScene(new Scene(root, 550, 400));

                // Opzionale: Centra la nuova finestra
                newStage.centerOnScreen();

                // Mostra la nuova finestra senza chiudere quella attuale
                newStage.show();
            } catch (IOException e) {
                feedbackProperty.set("Errore durante la risposta a tutti: " + e.getMessage());
            }
        } else {
            feedbackProperty.set("Seleziona un messaggio per rispondere a tutti");
        }
    }

    @FXML
    protected void onForwardButtonClick() throws IOException {
        Mail selectedMail = messageList.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            try {
                client.forward(selectedMail);
                
                // Apre la schermata di composizione con i campi precompilati
                FXMLLoader fxmlLoader = new FXMLLoader(MainClient.class.getResource("create_new_message-view.fxml"));
                Parent root = fxmlLoader.load();
                CreateNewMessageController createNewMessageController = fxmlLoader.getController();
                createNewMessageController.setClient(client);
                
                // Precompila i campi
                Mail forwardMail = new Mail();
                forwardMail.setSubject("Fwd: " + selectedMail.getSubject());
                forwardMail.setBody("\n\n\n\n\n    Il " + selectedMail.getDateTimeFormatted() + ", " + selectedMail.getSender() + " ha scritto: \n\n" + selectedMail.getBody() + "\n");
                
                createNewMessageController.precompileFields(forwardMail);

                // Crea un nuovo Stage (nuova finestra)
                Stage newStage = new Stage();
                newStage.setTitle("Scrivi nuovo messaggio <" + client.getUser() + ">");
                newStage.setScene(new Scene(root, 550, 400));

                // Opzionale: Centra la nuova finestra
                newStage.centerOnScreen();

                // Mostra la nuova finestra senza chiudere quella attuale
                newStage.show();
            } catch (IOException e) {
                feedbackProperty.set("Errore durante l'inoltro: " + e.getMessage());
            }
        } else {
            feedbackProperty.set("Seleziona un messaggio da inoltrare");
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        Mail selectedMail = messageList.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            try {
                int result = client.deleteMail(selectedMail);
                if (result == 0) {
                    // Aggiorna la lista delle email dopo l'eliminazione
                    messageList.getItems().remove(selectedMail);
                    // Non pulisce i campi di visualizzazione, così mostra subito i dati della mail appena selezionata

                    feedbackProperty.set("Email eliminata con successo");
                } else {
                    feedbackProperty.set("Errore durante l'eliminazione dell'email: codice " + result);
                }
            } catch (IOException e) {
                feedbackProperty.set("Errore durante l'eliminazione dell'email: " + e.getMessage());
            }
        } else {
            feedbackProperty.set("Seleziona un messaggio da eliminare");
        }
    }
    
    @FXML
    private Label newMailsLabel;

    protected void showMails() {
        try {
            List<Mail> newMails =  client.getNewMails();
            int newMailCount = 0;
            
            for (Mail mail : newMails) {
                if (!messageList.getItems().contains(mail)) {
                    // Aggiungo nuova mail alla lista
                    messageList.getItems().add(mail);
                    newMailCount++;
                }
            }
            
            // Ordina la lista dopo aver aggiunto tutte le email
            if (newMailCount > 0) {
                Collections.sort(messageList.getItems());
                
                // Aggiorna il contatore totale di nuove email
                lastMailCount += newMailCount;
                
                // Aggiorna l'etichetta con il numero di nuove email
                Platform.runLater(() -> {
                    newMailsLabel.setText("Nuove email: " + lastMailCount);
                    newMailsLabel.setVisible(true);
                });
            }

        } catch (IOException e) {
            Platform.runLater(() -> {
                feedbackProperty.set("Errore durante il recupero delle email: " + e.getMessage());
            });
        }
    }
    
    protected void openMail() {
        Mail selectedMail = messageList.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            senderField.setText(selectedMail.getSender());
            recipientsField.setText(selectedMail.getRecipientsAsString());
            subjectField.setText(selectedMail.getSubject());
            messageContent.setText(selectedMail.getBody());

            // Imposta la mail come letta
            if (selectedMail.isNew()) {
                try {
                    client.setMailRead(selectedMail);
                    messageList.refresh();
                    
                    // Decrementa il contatore delle nuove email
                    lastMailCount--;
                    if (lastMailCount <= 0) {
                        lastMailCount = 0;
                        newMailsLabel.setVisible(false);
                    } else {
                        newMailsLabel.setText("Nuove email: " + lastMailCount);
                    }
                    
                } catch (Exception e) {
                    feedbackProperty.set("Errore durante il settaggio della mail come letta: " + e.getMessage());
                }
            }
        }
    }
    
    protected void loop() {
        running = true;
        checkMailThread = new Thread(() -> {
            while (running) {
                try {
                    // Verifica connessione al server con ping
                    boolean connected = client != null && client.isConnected();
                    
                    Platform.runLater(() -> {
                        connectionStatus.setText(connected ? "Connesso al server" : "Non connesso al server");
                        if (connected) {
                            showMails();
                        }
                    });
                    
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    running = false;
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        feedbackProperty.set("Errore durante il controllo delle email: " + e.getMessage());
                    });
                }
            }
        });
        checkMailThread.setDaemon(true);
        checkMailThread.start();
    }

    protected void getMail() {
        try {
            // Recupero le mail in MailBox
            List<Mail> allMails = client.getMails();
            // Aggiorna la lista delle email
            messageList.getItems().setAll(allMails);
        } catch (IOException e) {
            feedbackProperty.set("Errore durante il recupero delle email: " + e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        feedbackProperty = new SimpleStringProperty("");
        errorLabel.textProperty().bind(feedbackProperty);
        
        // Inizializza il contatore delle nuove email e nasconde l'etichetta
        lastMailCount = 0;
        newMailsLabel.setVisible(false);

        // Aggiungi listener per la selezione nella lista delle mail
        messageList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openMail();
            }
        });

        // Set custom cell factory
        messageList.setCellFactory(new Callback<ListView<Mail>, ListCell<Mail>>() {
            @Override
            public ListCell<Mail> call(ListView<Mail> listView) {
                return new ListCell<Mail>() {
                    @Override
                    protected void updateItem(Mail mail, boolean empty) {
                        super.updateItem(mail, empty);
                        if (empty || mail == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(mail.toString());
                            if (mail.isNew()) {
                                setStyle("-fx-font-weight: bold;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                };
            }
        });
    }
}