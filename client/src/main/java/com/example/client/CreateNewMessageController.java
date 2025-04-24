package com.example.client;

import com.example.client.model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CreateNewMessageController {

    private ClientMainModel client;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField recipientsField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea messageContent;

    @FXML
    private Button sendButton;

    @FXML
    private Button trashDraftButton;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[a-z]+$";

    public void setClient(ClientMainModel client) {
        this.client = client;
    }

    /**
     * Precompila i campi del form con i dati di una mail
     * Utilizzato per risposta, risposta a tutti e inoltro
     * @param mail La mail con i dati precompilati
     */
    public void precompileFields(Mail mail) {
        if (mail != null) {
            if (mail.getRecipients() != null && !mail.getRecipients().isEmpty()) {
                recipientsField.setText(String.join(" ; ", mail.getRecipients()));
            }
            if (mail.getSubject() != null) {
                subjectField.setText(mail.getSubject());
            }
            if (mail.getBody() != null) {
                messageContent.setText(mail.getBody());
            }
        }
    }

    @FXML
    protected void onSendButtonClick() throws IOException {
        if (recipientsField.getText().isEmpty()) {
            errorLabel.setText("Inserire un destinatario");
            return;
        }

        if (messageContent.getText().isEmpty()) {
            errorLabel.setText("Inserire un messaggio");
            return;
        }

        if (subjectField.getText().isEmpty()) {
            errorLabel.setText("Inserire un oggetto");
            return;
        }

        //Destinatari separati da ";"
        String[] recipientArray = recipientsField.getText().split("\\s*;\\s*");
        Set<String> recipientSet = new HashSet<>();
        List<String> recipients = new ArrayList<>();

        //Verifica la correttezza di ogni indirizzo email
        for (String recipient : recipientArray) {
            if (!Pattern.compile(EMAIL_REGEX).matcher(recipient).matches()) {
                errorLabel.setText("Email non ben formattata: " + recipient);
                return;
            }

            if (!recipientSet.add(recipient)) { // Set.add() ritorna false se l'email è duplicata
                errorLabel.setText("Destinatario duplicato: " + recipient);
                return;
            }

            recipients.add(recipient);
        }

        //Creazione oggetto mail
        Mail mail = new Mail();
        mail.setRecipients(recipients);
        mail.setSubject(subjectField.getText());
        mail.setBody(messageContent.getText());
        mail.setDateTime(System.currentTimeMillis());
        mail.setNew(true);

        int sendMail = client.sendMail(mail);

        switch (sendMail) {
            case 0:
                //email inviata correttamente, chiude la pagina
                Stage stage = (Stage) recipientsField.getScene().getWindow();
                stage.close();
                break;
            case 3:
                errorLabel.setText("Utente non loggato");
                break;
            case 8:
                errorLabel.setText("Destinatario errato");
                break;
            case 9:
                errorLabel.setText("Email non inviata a tutti i destinatari");
                break;
            case 500:
                errorLabel.setText("Server non raggiungibile");
                break;
            default:
                errorLabel.setText("ERROR");
                break;
        }
    }

    @FXML
    protected void onDeleteDraftButtonClick(){
        //chiude la pagina
        Stage stage = (Stage) recipientsField.getScene().getWindow();
        stage.close();
    }

}