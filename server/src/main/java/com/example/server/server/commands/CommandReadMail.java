package com.example.server.server.commands;

import com.example.server.MainServer;
import com.example.server.model.Mail;
import com.example.server.server.ServerFeedback;
import com.example.server.server.Task;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class CommandReadMail implements ICommand{
    @Override
    public void execute(Task task, List<JSONObject> params) throws Exception {

        String user = task.getUser();

        if (user == null) {
            task.sendFeedback(ServerFeedback.NO_USER, "No user logged in");
            return;
        }

        Mail mail = Mail.getFromJson(params.get(0));

        if (mail == null) {
            task.sendFeedback(ServerFeedback.WRONG_EMAIL, "Invalid mail");
            return;
        }

        Lock readLock = MainServer.getModel().getLocks().getReadLock(user);

        try {
            readLock.lock();

            if (MainServer.getModel().getFileSystem().readMail(user, mail)) {
                task.sendFeedback(ServerFeedback.OK, "Mail read");
            } else {
                task.sendFeedback(ServerFeedback.WRONG_EMAIL, "Mail not found");
            }

        } finally {
            readLock.unlock();
        }

    }
}
