package com.example.client.network;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;

public class ConnectionHandler {
    private String host;
    private int port;
    private String user;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean error;
    private boolean connection;

    public ConnectionHandler(String host, int port, String user) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.error = false;
        this.connection = true;
    }

    public JSONObject sendRequest(JSONObject request) throws IOException {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            writer.println(request.toString());
            writer.flush();

            String feedback = reader.readLine();
            if (feedback != null) {
                connection = true; // Connection successful
                return new JSONObject(feedback);
            }
            return null;
        } catch (Exception e) {
            connection = false; // Update connection status on failure
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isConnected() {
        return connection;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public JSONObject receiveResponse() throws IOException {
        if (!connection) {
            throw new IOException("Connection closed");
        }

        String response = reader.readLine();
        if (response == null || response.isBlank()) {
            throw new IOException("Empty response");
        }

        return new JSONObject(response);
    }
}