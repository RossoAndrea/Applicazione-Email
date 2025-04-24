package com.example.server.server.commands;

import com.example.server.MainServer;
import com.example.server.locks.Locks;
import com.example.server.model.Mail;
import com.example.server.server.ServerFeedback;
import com.example.server.server.Task;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class CommandSendMail implements ICommand{
    @Override
    public void execute(Task task, List<JSONObject> params) throws Exception {

        String user = task.getUser();

        if (user == null) {
            task.sendFeedback(ServerFeedback.NO_USER, "No user logged in");
            return;
        }

        Locks<String> rx = MainServer.getModel().getLocks();
        Mail mail = Mail.getFromJson(params.get(0));

        if (mail.getRecipients() == null || mail.getRecipients().isEmpty()) {
            task.sendFeedback(ServerFeedback.WRONG_RECIPIENTS, "No recipients");
            return;
        }

        for (String recipient : mail.getRecipients()) {
            if (MainServer.getModel().getFileSystem().checkUser(recipient)) {
                rx.createLock(recipient);
                Lock writeLock = rx.getWriteLock(recipient);
                try {
                    writeLock.lock();
                    MainServer.getModel().getFileSystem().saveMail(recipient, mail, "INBOX");
                } finally {
                    writeLock.unlock();
                }
            } else {
                Mail errorMail = new Mail();
                errorMail.setDateTime(System.currentTimeMillis());
                errorMail.setSubject("Errore nell'invio della mail a " + recipient);
                String body = "Da: " + mail.getSender() + "\n" + "A: " + recipient + "\n" + "Oggetto: " + mail.getSubject() + "\n" + "Testo: " + mail.getBody();
                errorMail.setBody(body);
                errorMail.setSender("Server");
                errorMail.setRecipients(List.of(user));
                errorMail.setNew(true);
                MainServer.getModel().getFileSystem().saveMail(user, errorMail, "INBOX");
            }
        }

        task.sendFeedback(ServerFeedback.OK, "Mail sent");
    }
}
