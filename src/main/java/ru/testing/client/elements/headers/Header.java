package ru.testing.client.elements.headers;

/**
 * Http header
 */
public class Header {

    private String headerName;
    private String headerValue;

    public Header(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    /**
     * Get header name
     * @return String
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * Get header value
     * @return String
     */
    public String getHeaderValue() {
        return headerValue;
    }
}
