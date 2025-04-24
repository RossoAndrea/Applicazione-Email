package com.example.server.server.commands;

import com.example.server.MainServer;
import com.example.server.model.Mail;
import com.example.server.server.ServerFeedback;
import com.example.server.server.Task;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class CommandDeleteMail implements ICommand{
    @Override
    public void execute(Task task, List<JSONObject> params) throws Exception {

        if (task.getUser() == null) {
            task.sendFeedback(ServerFeedback.NO_USER, "No user logged in");
            return;
        }

        String user = task.getUser();
        Mail mail = Mail.getFromJson(params.get(0));
        Lock readLock = MainServer.getModel().getLocks().getReadLock(user);
        boolean deleteOk = false;

        try {
            readLock.lock();

            if (MainServer.getModel().getFileSystem().deleteMail(user, "Mailbox", mail.getDateTime())) {
                deleteOk = true;
            }

        } finally {
            readLock.unlock();
        }

        if (deleteOk) {
            task.sendFeedback(ServerFeedback.OK, "Mail deleted");
        } else {
            task.sendFeedback(ServerFeedback.WRONG_EMAIL, "Mail not found");
        }

    }
}
