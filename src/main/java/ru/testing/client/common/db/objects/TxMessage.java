package ru.testing.client.common.db.objects;

/**
 * Transmit messages list for save in database
 */
public class TxMessage {

    private int id;
    private int sessionId;
    private String value;

    public TxMessage(int sessionId, String value) {
        this.sessionId = sessionId;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getValue() {
        return value;
    }
}
