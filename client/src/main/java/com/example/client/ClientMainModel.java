package com.example.client;

import com.example.client.config.Config;
import com.example.client.config.ServerCommand;
import com.example.client.model.Mail;
import com.example.client.network.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientMainModel {
    private String user;
    private List<Mail> mails;
    private ConnectionHandler connection;
    private Config config;
    private String host;
    private int port;

    public ClientMainModel(){
        this.mails = new ArrayList<>();

        config = new Config();
        this.host = config.getProperty("srv.address");
        this.port = Integer.parseInt(config.getProperty("srv.port"));

        this.connection = new ConnectionHandler(this.host, this.port, this.user);
    }

    public boolean isConnected() {
        try {
            
            // Then verify with an actual ping to the server
            CommandToServer<String> cmd = new CommandToServer<>(this.user);
            cmd.setServerCommand(ServerCommand.PING.getValue());
            JSONObject response = connection.sendRequest(cmd.toJson());
            return response != null && response.getInt("code") == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int checkUser(String user) throws IOException {
        CommandToServer<String> cmd = new CommandToServer<>(user);
        cmd.setServerCommand(ServerCommand.LOGIN.getValue());

        try {
            JSONObject response = connection.sendRequest(new JSONObject(cmd));
            if (response != null) {
                return response.getInt("code");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 999;
    }

    public List<Mail> getNewMails() throws IOException {
        CommandToServer<String> cmd = new CommandToServer<>(this.user);
        cmd.setServerCommand(ServerCommand.NEW_MAILS.getValue());

        JSONObject response = connection.sendRequest(new JSONObject(cmd));
        if (response != null && response.getInt("code") == 0) {
            mails.clear();
            JSONArray jsonArray = response.getJSONArray("mails");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonMail = jsonArray.getJSONObject(i);
                Mail mail = new Mail();
                mail.setSender(jsonMail.getString("sender"));
                mail.setRecipients(jsonMail.getJSONArray("recipients").toList().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
                mail.setSubject(jsonMail.getString("subject"));
                mail.setBody(jsonMail.getString("body"));
                mail.setDateTime((Long) jsonMail.get("dateTime"));
                mail.setNew(jsonMail.getBoolean("isNew"));
                mails.add(mail);
            }
            return new ArrayList<>(mails);
        }
        return List.of();
    }

    public List<Mail> getMails() throws IOException {
        CommandToServer<String> cmd = new CommandToServer<>(this.user);
        cmd.setServerCommand(ServerCommand.GET_EMAILS.getValue());

        JSONObject response = connection.sendRequest(new JSONObject(cmd));
        if (response != null && response.getInt("code") == 0) {
            mails.clear();
            JSONArray jsonArray = response.getJSONArray("mails");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonMail = jsonArray.getJSONObject(i);
                Mail mail = new Mail();
                mail.setSender(jsonMail.getString("sender"));
                mail.setRecipients(jsonMail.getJSONArray("recipients").toList().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
                mail.setSubject(jsonMail.getString("subject"));
                mail.setBody(jsonMail.getString("body"));
                mail.setDateTime((Long) jsonMail.get("dateTime"));
                mail.setNew(jsonMail.getBoolean("isNew"));
                mails.add(mail);
            }
            return new ArrayList<>(mails);
        }
        return List.of();
    }

    public void setMailRead(Mail selectedMail) throws IOException {
        CommandToServer<Mail> cmd = new CommandToServer<>(this.user);
        cmd.setServerCommand(ServerCommand.READ_EMAIL.getValue());
        cmd.addParams(selectedMail);

        JSONObject response = connection.sendRequest(cmd.toJson());
        if (response != null && response.getInt("code") == 0) {
            selectedMail.setNew(false);
        }
    }

    public int sendMail(Mail mail) throws IOException {
        if (mail != null) {
            mail.setSender(this.user);
            CommandToServer<Mail> cmd = new CommandToServer<>(this.user);
            cmd.setServerCommand(ServerCommand.SEND_EMAIL.getValue());
            cmd.addParams(mail);

            JSONObject response = connection.sendRequest(cmd.toJson());
            if (response != null) {
                int code = response.getInt("code");
                if (code == 0) {
                    mails.add(mail);
                }
                return code;
            }
            return 500;
        }
        return 999;
    }

    public int deleteMail(Mail mail) throws IOException {
        if (mail != null) {
            CommandToServer<Mail> cmd = new CommandToServer<>(this.user);
            cmd.setServerCommand(ServerCommand.DELETE_EMAIL.getValue());
            cmd.addParams(mail);

            JSONObject response = connection.sendRequest(cmd.toJson());
            if (response != null) {
                int code = response.getInt("code");
                if (code == 0) {
                    mails.remove(mail);
                }
                return code;
            }
        }
        return 999;
    }

    public void reply(Mail m) throws IOException {
        Mail mail = new Mail();
        mail.setSender(this.user);
        mail.setRecipients(List.of(m.getSender()));
        mail.setSubject("Re: " + m.getSubject());
        mail.setBody("\n\n\n\n\n    Il " + m.getDateTimeFormatted() + ", " + m.getSender() + " ha scritto: \n\n" + m.getBody() + "\n");
    }

    public void replyAll(Mail m) throws IOException {
        Mail mail = new Mail();
        mail.setSender(this.user);
        mail.setRecipients(m.getRecipients());
        mail.setSubject("Re: " + m.getSubject());
        mail.setBody("\n\n\n\n\n    Il " + m.getSender() + ", " + " ha scritto: \n\n" + m.getBody() + "\n" + "(" + m.getDateTimeFormatted() + ")");
    }


    public void forward(Mail m) throws IOException{
        Mail mail = new Mail();
        mail.setSender(this.user);
        mail.setSubject("Fwd: " + m.getSubject());
        mail.setBody("\n\n\n\n\n    Il " + m.getSender() + ", " + " ha scritto: \n\n" + m.getBody() + "\n" + "(" + m.getDateTimeFormatted() + ")");
    }

}
