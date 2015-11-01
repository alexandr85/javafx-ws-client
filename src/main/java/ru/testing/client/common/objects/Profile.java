package ru.testing.client.common.objects;

import java.util.List;

/**
 * Profile object
 */
public class Profile {

    private String serverUrl;
    private List<String> historyItem;
    private boolean autoScroll;
    private boolean filter;
    private List<String> filterItem;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public List<String> getHistoryItem() {
        return historyItem;
    }

    public void setHistoryItem(List<String> historyItem) {
        this.historyItem = historyItem;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public List<String> getFilterItem() {
        return filterItem;
    }

    public void setFilterItem(List<String> filterItem) {
        this.filterItem = filterItem;
    }
}
