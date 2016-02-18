package ru.testing.client.common.db.objects;

/**
 * Headers list for save in database
 */
public class Header {

    private int sessionId;
    private String name;
    private String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Header(int sessionId, String name, String value) {
        this.sessionId = sessionId;
        this.name = name;
        this.value = value;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
