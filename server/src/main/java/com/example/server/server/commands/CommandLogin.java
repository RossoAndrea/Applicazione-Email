package com.example.server.server.commands;

import com.example.server.MainServer;
import com.example.server.server.ServerFeedback;
import com.example.server.server.Task;
import org.json.JSONObject;

import java.util.List;

public class CommandLogin implements ICommand {

    @Override
    public void execute(Task task, List<JSONObject> params) throws Exception {
        if (MainServer.getModel().getFileSystem().checkUser(task.getUser())){
            task.sendFeedback(ServerFeedback.OK, "User logged in");
        } else {
            task.sendFeedback(ServerFeedback.WRONG_EMAIL, "User not found");
        }
    }
}
