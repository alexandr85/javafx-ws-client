package ru.testing.client.gui;

import javafx.beans.property.SimpleStringProperty;

/**
 * Message object for save in history table
 */
public class SendMessage {

    private final SimpleStringProperty message;

    /**
     * Default simple string message constructor
     * @param message String
     */
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
