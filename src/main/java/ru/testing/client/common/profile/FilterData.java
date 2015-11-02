package ru.testing.client.common.profile;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class FilterData {

    private List<ItemElement> item;
    private boolean filterOn;

    public FilterData() {

    }

    public FilterData(boolean filterOn, List<ItemElement> item) {
        this.filterOn = filterOn;
        this.item = item;
    }

    public FilterData(List<ItemElement> item) {
        this.item = item;
    }

    public FilterData(boolean filterOn) {
        this.filterOn = filterOn;
    }

    public List<ItemElement> getItem() {
        return item;
    }

    @XmlElement(name = "item")
    public void setItem(List<ItemElement> item) {
        this.item = item;
    }

    public boolean isFilterOn() {
        return filterOn;
    }

    @XmlAttribute(name = "filter_on")
    public void setFilterOn(boolean filterOn) {
        this.filterOn = filterOn;
    }
}
