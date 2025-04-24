package com.example.server.server;

public enum ServerFeedback {
    OK(0),
    WRONG_JSON(1),
    NO_COMMAND(2),
    NO_CREDENTIALS(3),
    WRONG_EMAIL(4),
    NO_USER(5),
    FS_ERROR(6),
    NO_EMAIL(7),
    WRONG_RECIPIENTS(8),
    NOT_ALL_MAILS_SENT(9),
    MAIL_NOT_FOUND(10)
    ;

    private final int value;

    private ServerFeedback(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
