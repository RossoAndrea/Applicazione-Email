package com.example.server.server.commands;

import com.example.server.server.Task;
import org.json.JSONObject;

import java.util.List;


public interface ICommand {
    void execute(Task task, List<JSONObject> params) throws Exception;
}
