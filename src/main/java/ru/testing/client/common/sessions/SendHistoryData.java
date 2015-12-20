package ru.testing.client.common.sessions;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class SendHistoryData {

    private List<ItemElement> item;

    public SendHistoryData() {

    }

    public SendHistoryData(List<ItemElement> item) {
        this.item = item;
    }

    public List<ItemElement> getItem() {
        return item;
    }

    @XmlElement(name = "item")
    public void setItem(List<ItemElement> item) {
        this.item = item;
    }
}
