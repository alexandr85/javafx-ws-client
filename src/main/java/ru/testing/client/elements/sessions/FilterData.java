package ru.testing.client.elements.sessions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class FilterData {

    private List<ItemElement> items;
    private boolean filterOn;

    public FilterData() {

    }

    public FilterData(boolean filterOn, List<ItemElement> items) {
        this.filterOn = filterOn;
        this.items = items;
    }

    public List<ItemElement> getItems() {
        return items;
    }

    @XmlElement(name = "item")
    public void setItems(List<ItemElement> items) {
        this.items = items;
    }

    public boolean isFilterOn() {
        return filterOn;
    }

    @XmlAttribute(name = "filter_on")
    public void setFilterOn(boolean filterOn) {
        this.filterOn = filterOn;
    }
}
