package ru.testing.client.elements.sessions.session;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Session {

    private String name;
    private ConnectionData connect;
    private SendHistoryData sendHistoryData;
    private FilterData filterData;

    public Session() {

    }

    public Session(String name, ConnectionData connect, SendHistoryData sendHistoryData, FilterData filterData) {
        this.name = name;
        this.connect = connect;
        this.sendHistoryData = sendHistoryData;
        this.filterData = filterData;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public ConnectionData getConnect() {
        return connect;
    }

    @XmlElement(name = "connect")
    public void setConnect(ConnectionData connect) {
        this.connect = connect;
    }

    public SendHistoryData getSendHistoryData() {
        return sendHistoryData;
    }

    @XmlElement(name = "send_history")
    public void setSendHistoryData(SendHistoryData sendHistoryData) {
        this.sendHistoryData = sendHistoryData;
    }

    public FilterData getFilterData() {
        return filterData;
    }

    @XmlElement(name = "filter")
    public void setFilterData(FilterData filterData) {
        this.filterData = filterData;
    }
}
