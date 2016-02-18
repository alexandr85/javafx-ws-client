package ru.testing.client.common.db.objects;

/**
 * Filter list for save in database
 */
public class Filter {

    private int sessionId;
    private String value;

    public Filter(int sessionId, String value) {
        this.sessionId = sessionId;
        this.value = value;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getValue() {
        return value;
    }
}
