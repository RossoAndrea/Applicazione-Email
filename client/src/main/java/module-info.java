module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;
    requires org.json;


    opens com.example.client to javafx.fxml;
    exports com.example.client;
    exports com.example.client.network;
    opens com.example.client.network to javafx.fxml;
}