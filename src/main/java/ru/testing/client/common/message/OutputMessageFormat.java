package ru.testing.client.common.message;

/**
 * Enum class for constant output message string format
 */
public enum OutputMessageFormat {

    DEFAULT("%-12s %s");

    String format;

    OutputMessageFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
