package ru.testing.client.elements.sessions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Session {

    private String name;
    private ServerData server;
    private SendHistoryData sendHistoryData;
    private OutputData outputData;
    private FilterData filterData;

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public ServerData getServer() {
        return server;
    }

    @XmlElement(name = "websocket")
    public void setServer(ServerData server) {
        this.server = server;
    }

    public SendHistoryData getSendHistoryData() {
        return sendHistoryData;
    }

    @XmlElement(name = "sendHistory")
    public void setSendHistoryData(SendHistoryData sendHistoryData) {
        this.sendHistoryData = sendHistoryData;
    }

    public OutputData getOutputData() {
        return outputData;
    }

    @XmlElement(name = "output")
    public void setOutputData(OutputData outputData) {
        this.outputData = outputData;
    }

    public FilterData getFilterData() {
        return filterData;
    }

    @XmlElement(name = "filter")
    public void setFilterData(FilterData filterData) {
        this.filterData = filterData;
    }
}
