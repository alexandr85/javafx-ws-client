package ru.testing.client.common.db.objects;

import java.util.List;

/**
 * Session object for save in database
 */
public class Session {

    private int id;
    private String name;
    private String url;
    private boolean filterOn;
    private boolean filterShow;
    private boolean autoScroll;
    private boolean barShow;
    private List<Filter> filters;
    private List<Header> headers;
    private List<RxMessage> rxMessages;
    private List<TxMessage> txMessages;

    public Session(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Session(String name, String url, boolean filterOn, boolean filterShow, boolean autoScroll, boolean barShow) {
        this.name = name;
        this.url = url;
        this.filterOn = filterOn;
        this.filterShow = filterShow;
        this.autoScroll = autoScroll;
        this.barShow = barShow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean getFilterOn() {
        return filterOn;
    }

    public boolean getFilterShow() {
        return filterShow;
    }

    public boolean getAutoScroll() {
        return autoScroll;
    }

    public boolean getBarShow() {
        return barShow;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public List<RxMessage> getRxMessages() {
        return rxMessages;
    }

    public void setRxMessages(List<RxMessage> rxMessages) {
        this.rxMessages = rxMessages;
    }

    public List<TxMessage> getTxMessages() {
        return txMessages;
    }

    public void setTxMessages(List<TxMessage> txMessages) {
        this.txMessages = txMessages;
    }
}
