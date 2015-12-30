package ru.testing.client.elements.sessions.session;

import javax.xml.bind.annotation.XmlAttribute;

public class ItemElement {

    private String value;

    public ItemElement() {

    }

    public ItemElement(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute(name = "value")
    public void setValue(String value) {
        this.value = value;
    }
}
