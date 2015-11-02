package ru.testing.client.common.profile;

import javax.xml.bind.annotation.XmlAttribute;

public class ServerData {

    private String url;

    public ServerData() {

    }

    public ServerData(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @XmlAttribute(name = "url")
    public void setUrl(String url) {
        this.url = url;
    }
}
