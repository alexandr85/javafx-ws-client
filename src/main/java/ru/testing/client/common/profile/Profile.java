package ru.testing.client.common.profile;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "profile")
public class Profile {

    private ServerData server;
    private SendHistoryData sendHistoryData;
    private OutputData outputData;
    private FilterData filterData;

    public Profile() {

    }

    public Profile(ServerData server, SendHistoryData sendHistoryData, OutputData outputData, FilterData filterData) {
        this.server = server;
        this.sendHistoryData = sendHistoryData;
        this.outputData = outputData;
        this.filterData = filterData;
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
