package com.example.server.server.commands;

import com.example.server.server.ServerFeedback;
import com.example.server.server.Task;
import org.json.JSONObject;

import java.util.List;

public class CommandPing implements ICommand {

    /*
    * Command to check if the server is up
    * @param task task info
    * @param params command parameters
    */
    @Override
    public void execute(Task task, List<JSONObject> params) {
        task.sendFeedback(ServerFeedback.OK,"Ping executed");
    }
}
