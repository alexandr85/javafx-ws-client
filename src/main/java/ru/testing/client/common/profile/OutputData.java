package ru.testing.client.common.profile;

import javax.xml.bind.annotation.XmlAttribute;

public class OutputData {

    private boolean autoScrollOn;

    public OutputData() {

    }

    public OutputData(boolean autoScrollOn) {
        this.autoScrollOn = autoScrollOn;
    }

    public boolean isAutoScrollOn() {
        return autoScrollOn;
    }

    @XmlAttribute(name = "auto_scroll_on")
    public void setAutoScrollOn(boolean autoScrollOn) {
        this.autoScrollOn = autoScrollOn;
    }
}