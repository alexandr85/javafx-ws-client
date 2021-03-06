package ru.testing.client.websocket;

/**
 * Enum class for constant output message string format
 */
public enum ReceivedMessageFormat {

    DEFAULT("%-12s %s");

    String format;

    ReceivedMessageFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
