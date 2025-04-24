package com.example.server.filesystem;

import com.example.server.model.Mail;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    private static final String INBOX = "Inbox";
    private static final String MAILBOX = "Mailbox";
    // private static final String USER_FILE = "user.cred";

    private Path rootDb = null;

    /**
     * Constructor of the FileSystem class
     * @param rootDbPath root path of the 'database'
     * @throws IOException
     */
    public FileSystem(String rootDbPath) throws IOException {
        try {
            rootDb = Paths.get(rootDbPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * check if the user exists
     * @param mail the mail of the user
     * @return true if it's ok
     */
    public boolean checkUser(String mail) {

        String path = rootDb + File.separator + mail;

        File folder = new File(path);

        return folder.exists() && folder.isDirectory();
    }

    public boolean saveMail(String user, Mail mail, String folder) {
        String path = rootDb + File.separator + user + File.separator + folder;
        File folderPath = new File(path);

        if (!folderPath.exists()) return false;

        try {
            Path filePath = Paths.get(folderPath + File.separator + mail.getDateTime() + ".mail");
            Files.writeString(filePath, mail.toJson().toString(), StandardOpenOption.CREATE_NEW);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<JSONObject> getMails(String user, String folder) {
        String path = rootDb + File.separator + user + File.separator + folder;
        File folderPath = new File(path);

        if (!folderPath.exists()) return null;

        List<JSONObject> mails = new ArrayList<>();
        for (File file : folderPath.listFiles()) {
            try {
                String content = Files.readString(file.toPath());
                mails.add(new JSONObject(content));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mails;
    }

    public boolean deleteMail(String user, String folder, long dateTime) {
        String path = rootDb + File.separator + user + File.separator + folder;
        File folderPath = new File(path);

        if (!folderPath.exists()) return false;

        File mailFile = new File(folderPath + File.separator + dateTime + ".mail");
        return mailFile.delete();
    }

    public boolean readMail(String user, Mail mail) {
        if (deleteMail(user, "Inbox", mail.getDateTime())) {
            mail.setNew(false);
            return saveMail(user, mail, "Mailbox");
        }
        return false;
    }
}
