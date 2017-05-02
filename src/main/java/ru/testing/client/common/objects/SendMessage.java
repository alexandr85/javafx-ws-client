package ru.testing.client.common.objects;

/**
 * Sent message object
 */
public class SendMessage {

    private boolean autoSend;
    private String value;

    public SendMessage(boolean autoSend, String value) {
        this.autoSend = autoSend;
        this.value = value;
    }

    public boolean isAutoSend() {
        return autoSend;
    }

    public String getValue() {
        return value;
    }
}
