package com.example.client;

import com.example.client.model.Mail;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommandToServer<T> {
    private String user;
    private int serverCommand;
    private ArrayList<T> params;

    public CommandToServer(String user) {
        this.user = user;
        this.params = new ArrayList<>();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getServerCommand() {
        return serverCommand;
    }

    public void setServerCommand(int serverCommand) {
        this.serverCommand = serverCommand;
    }

    public ArrayList<T> getParams() {
        return params;
    }

    public void setParams(ArrayList<T> params) {
        this.params = params;
    }

    public void addParams(T param) {
        this.params.add(param);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("user", user);
        json.put("serverCommand", serverCommand);

        JSONArray jsonParams = new JSONArray();
        for (T param : params) {
            if (param instanceof Mail) {
                jsonParams.put(((Mail) param).toJson()); // Usa il metodo toJson() di Mail
            } else {
                jsonParams.put(param.toString()); // Per gli altri tipi, usa toString()
            }
        }
        json.put("params", jsonParams);
        return json;
    }
}