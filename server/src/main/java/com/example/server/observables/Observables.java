package com.example.server.observables;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Observables<T> {

    private ObservableList<T> lists;

    /**
     * Constructor
     */
    public Observables() {
        lists = FXCollections.observableArrayList();
    }

    /**
     * Creates a new list
     * @return the new list
     */
    public ObservableList<?> newList() {
        return FXCollections.observableArrayList();
    }

    /**
     * Adds a new item to the list
     * @param value the value to add
     */
    public void addListItem(T value) {
        if (lists == null) return;

        if (lists.size() > 1000) {
            clearList();
        }

        run(() -> lists.add(0, value));
    }

    /**
     * Clears the list
     */
    public void clearList() {
        if (lists == null) return;

        run(lists::clear);
    }

    /**
     * Returns the list
     * @return the list
     */
    public ObservableList<T> getList() {
        return lists;
    }

    void run(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
