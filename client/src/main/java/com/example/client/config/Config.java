package com.example.client.config;

import java.io.*;
import java.util.Properties;

public class Config {
    private final File configFile;
    private final Properties properties;

    public Config() {
        File path = getModuleDir();
        properties = new Properties();
        configFile = new File(path, "config.cfg");

        if (!configFile.exists()) {
            properties.setProperty("srv.port", "12100");
            properties.setProperty("srv.address", "127.0.0.1");
            save();
        } else {
            load();
        }
    }

    private File getModuleDir() {
        try {
            String path = System.getProperty("user.dir");
            return new File(path);
        } catch (Exception e) {
            return new File(".");
        }
    }

    public void save() {
        try {
            OutputStream outputStream = new FileOutputStream(configFile);
            properties.store(outputStream, "Client Configuration");
        } catch (Exception ex) {
            ex.printStackTrace();


        }
    }

    public void load() {
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