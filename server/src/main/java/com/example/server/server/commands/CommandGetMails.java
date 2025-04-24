package com.example.server.server.commands;

import com.example.server.MainServer;
import com.example.server.model.Mail;
import com.example.server.server.ServerFeedback;
import com.example.server.server.Task;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class CommandGetMails implements ICommand{
    @Override
    public void execute(Task task, List<JSONObject> params) throws Exception {

        String user = task.getUser();

        if (user == null) {
            task.sendFeedback(ServerFeedback.NO_USER, "No user logged in");
            return;
        }

        Lock readLock = MainServer.getModel().getLocks().getReadLock(user);
        List<Mail> mails = new ArrayList<>();
        try {
            readLock.lock();
            List<JSONObject> mailboxMails = MainServer.getModel().getFileSystem().getMails(user, "MAILBOX");

            for (JSONObject mail : mailboxMails) {
                Mail newMail = Mail.getFromJson(mail);
                newMail.setNew(false);
                mails.add(newMail);
            }
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        } finally {
            readLock.unlock();
        }

        Collections.sort(mails);

        task.sendFeedback(ServerFeedback.OK, mails);
    }
}
