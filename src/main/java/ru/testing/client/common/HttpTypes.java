package ru.testing.client.common;

/**
 * Http types client
 */
public enum HttpTypes {
    WEBSOCKET("WS"),
    HTTP_GET("GET"),
    HTTP_POST("POST");

    String name;

    HttpTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
