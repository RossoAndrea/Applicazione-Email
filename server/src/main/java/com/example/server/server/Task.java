package com.example.server.server;
import com.example.server.MainServer;
import com.example.server.model.Mail;
import com.example.server.server.commands.*;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Task implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private final Socket socket;
    private List<ICommand> commands;
    private String user;

    public Task(Socket socket) {

        commands = new ArrayList<>();
        commands.add(new CommandPing());
        commands.add(new CommandLogin());
        commands.add(new CommandGetMails());
        commands.add(new CommandReadMail());
        commands.add(new CommandSendMail());
        commands.add(new CommandDeleteMail());
        commands.add(new CommandNewMail());

        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        user = null;
    }

    @Override
    public void run() {
        try {


            String data = in.readLine();
            if (data.isBlank()) return;

            try {
                JSONObject jData = new JSONObject(data);

                log(data);
                verifyJSON(jData);

            } catch (Exception e) {
                sendFeedback(ServerFeedback.WRONG_JSON, "Invalid JSON");
                log("Invalid JSON: " + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void verifyJSON(JSONObject jData) throws Exception {

        // Controllo se il campo user è presente
        if (!jData.has("user")) {
            sendFeedback(ServerFeedback.NO_CREDENTIALS, "Missing user");
            return;
        }

        String user = jData.getString("user");
        if (user == null) {
            sendFeedback(ServerFeedback.NO_USER, "Invalid user");
            return;
        }

        if (!MainServer.getModel().getFileSystem().checkUser(user)) {
            sendFeedback(ServerFeedback.NO_USER, "User not found");
            return;
        }

        this.user = user;
        MainServer.getModel().getLocks().createLock(user);

        // Controllo se il campo command è presente
        if (!jData.has("serverCommand")) {
            sendFeedback(ServerFeedback.NO_COMMAND, "Missing command");
            return;
        }

        // Recupero il comando
        ServerCommand command = ServerCommand.values()[jData.getInt("serverCommand")];
        if (command == null) {
            sendFeedback(ServerFeedback.NO_COMMAND, "Invalid command");
            return;
        }

        List<JSONObject> params = new ArrayList<>();
        if (jData.has("params")) {
            for (int i = 0; i < jData.getJSONArray("params").length(); i++) {
                params.add(jData.getJSONArray("params").getJSONObject(i));
            }
        }

        // eseguo il comando
        commands.get(command.getValue()).execute(this, params);
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFeedback(ServerFeedback fb, String message) {
        JSONObject jData = new JSONObject();
        jData.put("code", fb.getValue());
        jData.put("message", message);
        out.println(jData.toString());
        log(jData.toString());
    }

    public void sendFeedback(ServerFeedback fb, List<Mail> mails) {
        JSONObject jData = new JSONObject();
        jData.put("code", fb.getValue());
        jData.put("mails", new ArrayList<>());
        for (Mail mail : mails) {
            jData.append("mails", mail.toJson());
        }
        out.println(jData.toString());
        log(jData.toString());
    }

    private void log(String message) {
        MainServer.log(message);
    }

    public String getUser() {
        return user;
    }
}
