package ru.testing.client.elements.sessions.session;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ConnectionData {

    private String url;
    private List<ItemElement> headers;

    public ConnectionData() {

    }

    public ConnectionData(String url, List<ItemElement> headers) {
        this.url = url;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    @XmlAttribute(name = "url")
    public void setUrl(String url) {
        this.url = url;
    }

    public List<ItemElement> getHeaders() {
        return headers;
    }

    @XmlElement(name = "header")
    public void setHeaders(List<ItemElement> headers) {
        this.headers = headers;
    }
}
