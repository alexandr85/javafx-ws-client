package ru.testing.client.common.objects;

/**
 * Http parameter object
 */
public class HttpParameter {

    private String name;
    private String value;

    public HttpParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
