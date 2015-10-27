package ru.testing.client.commons;

/**
 * Enum class for constant output message string format
 */
public enum OutputFormat {

    DEFAULT("%-12s %s");

    String format;

    OutputFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
