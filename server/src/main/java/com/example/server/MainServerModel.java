package com.example.server;

import com.example.server.config.Config;
import com.example.server.filesystem.FileSystem;
import com.example.server.locks.Locks;
import com.example.server.observables.Observables;

import java.io.IOException;

public class MainServerModel {

    private Observables<String> observables;

    private Config config;

    private FileSystem fileSystem;

    private Locks<String> locks;

    public MainServerModel() throws IOException {
        observables = new Observables<>();
        config = new Config();
        fileSystem = new FileSystem(config.getProperty("fs.path"));
        locks = new Locks<>();
    }

    /**
     * Returns the observables
     * @return the observables
     */
    public Observables<String> getObservables() {
        return observables;
    }

    public Config getConfig() {
        return config;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public Locks<String> getLocks() {
        return locks;
    }
}
