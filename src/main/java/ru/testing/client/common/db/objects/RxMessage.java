package ru.testing.client.common.db.objects;

/**
 * Received messages list for save in database
 */
public class RxMessage {

    private int id;
    private int sessionId;
    private String time;
    private String value;

    public RxMessage(String time, String value) {
        this.time = time;
        this.value = value;
    }

    public RxMessage(int sessionId, String time, String value) {
        this.sessionId = sessionId;
        this.time = time;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }
}
