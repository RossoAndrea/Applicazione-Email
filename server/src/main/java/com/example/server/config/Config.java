package com.example.server.config;

import java.io.*;
import java.util.Properties;

public class Config {

    private final File configFile;

    private final Properties properties;

    /**
     * Constructor of the Config class
     */
    public Config() {
        File path = getModuleDir();
        properties = new Properties();
        configFile = new File(path,"config.cfg");

        if (!configFile.exists()) {
            properties.setProperty("fs.path", path.getPath()  + "\\Data");
            properties.setProperty("srv.port", "12100");
            properties.setProperty("srv.threads", "5");
            properties.setProperty("srv.timeout", "5000");
            save();
        } else {
            load();
        }
    }

    /**
     * Leggo la cartella base dell'applicazione
     * @return cartella base
     */
    private File getModuleDir() {
        try {
            String path = System.getProperty("user.dir");
            return new File(path);
        } catch (Exception e) {
            return new File(".");
        }
    }

    private void save() {
        try {
            OutputStream outputStream = new FileOutputStream(configFile);
            properties.store(outputStream, "Server Configuration");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void load() {
        try {
            InputStream inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String p) {
        return properties.getProperty(p, "");
    }

}
