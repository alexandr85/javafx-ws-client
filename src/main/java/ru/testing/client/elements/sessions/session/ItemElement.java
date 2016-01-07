package ru.testing.client.elements.sessions.session;

import javax.xml.bind.annotation.XmlAttribute;

public class ItemElement {

    private String name;
    private String value;

    public ItemElement() {

    }

    public ItemElement(String value) {
        this.value = value;
    }

    public ItemElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @XmlAttribute(name = "value")
    public void setValue(String value) {
        this.value = value;
    }
}
