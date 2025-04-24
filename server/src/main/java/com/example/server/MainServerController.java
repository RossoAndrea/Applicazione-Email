package com.example.server;

import javafx.fxml.FXML;

import javafx.scene.control.ListView;

public class MainServerController {

    @FXML
    private ListView<String> listLog;

    private MainServerModel model;

    /**
     * Clears the list view
     */
    @FXML
    protected void onClearButtonClick()
    {
        MainServer.getModel().getObservables().clearList();
        System.out.println("List cleared");
    }

    /**
     * Initializes the model and binds the list log
     * @param model the model
     */
    public void initModel(MainServerModel model) {
        this.model = model;
        listLog.setItems(model.getObservables().getList());
    }

}
