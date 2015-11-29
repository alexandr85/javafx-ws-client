package ru.testing.client.common;

/**
 * Application types constants
 */
public enum ApplicationType {

    CONSOLE, GUI;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
