package ru.testing.client.gui;

import javafx.beans.property.SimpleStringProperty;

public class SendMessage {

    private final SimpleStringProperty message;

    public SendMessage(String message) {
        this.message = new SimpleStringProperty(message);
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
