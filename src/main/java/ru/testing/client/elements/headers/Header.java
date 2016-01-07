package ru.testing.client.elements.headers;

/**
 * Http header
 */
public class Header {

    private String name;
    private String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Get header name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Get header value
     * @return String
     */
    public String getValue() {
        return value;
    }
}
