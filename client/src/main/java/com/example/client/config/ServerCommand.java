package com.example.client.config;

public enum ServerCommand {
    PING(0),
    LOGIN(1),
    GET_EMAILS(2),
    READ_EMAIL(3),
    SEND_EMAIL(4),
    DELETE_EMAIL(5),
    NEW_MAILS(6)
    ;

    private final int value;
    private ServerCommand(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
