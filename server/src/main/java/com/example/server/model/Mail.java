package com.example.server.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Mail implements Serializable, Comparable<Mail> {
    private String sender;
    private List<String> recipients;
    private String subject;
    private String body;
    private long dateTime;
    private boolean isNew;

    public Mail() {}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public long getDateTime() {
        return dateTime;
    }

    public String getDateTimeFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(dateTime);
    }

    public void setDateTime(long value) {
        this.dateTime = value;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("sender", sender);
        json.put("recipients", new JSONArray(recipients));
        json.put("subject", subject);
        json.put("body", body);
        json.put("dateTime", dateTime);
        json.put("isNew", isNew);
        return json;
    }

    public static Mail getFromJson(JSONObject json) throws ParseException{
        if(json == null) return null;

        Mail mail = new Mail();
        mail.setSender(json.getString("sender"));
        JSONArray jRecipients = json.getJSONArray("recipients");
        mail.setRecipients(jRecipients.toList().stream().map(Object::toString).collect(Collectors.toList()));
        mail.setSubject(json.getString("subject"));
        mail.setBody(json.getString("body"));
        mail.setDateTime(json.getLong("dateTime"));
        mail.setNew(json.getBoolean("isNew"));
        return mail;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mail)) return false;

        Mail mail = (Mail) obj;
        return Objects.equals(body, mail.getBody());
    }

    @Override
    public String toString() {
        return getDateTimeFormatted() + "\t\t\t" + sender + "\t\t\t" + subject;
    }

    public List<String> RemoveRecipient(String user){
        List<String> res= recipients;
        res.remove(user);
        return res;
    }

    public String getRecipientsAsString() {
        String res = "";
        if (recipients!=null) {
            res = recipients.get(0);
            for (int i = 1; i < recipients.size(); i++) {
                res = res + ", " + recipients.get(i);
            }
        }
        return res;
    }

    @Override
    public int compareTo(Mail o) {
        return Long.compare(this.getDateTime(), o.getDateTime());
    }
}
