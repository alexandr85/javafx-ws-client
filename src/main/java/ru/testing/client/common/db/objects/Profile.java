package ru.testing.client.common.db.objects;

/**
 * Profile object for save in database
 */
public class Profile {

    private int id;
    private String name;
    private String url;
    private boolean autoScroll;
    private boolean barShow;
    private boolean filterShow;
    private boolean filterOn;

    public Profile(int id, String name, String url, boolean autoScroll,
                   boolean barShow, boolean filterShow, boolean filterOn) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.autoScroll = autoScroll;
        this.barShow = barShow;
        this.filterShow = filterShow;
        this.filterOn = filterOn;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public boolean isBarShow() {
        return barShow;
    }

    public void setBarShow(boolean barShow) {
        this.barShow = barShow;
    }

    public boolean isFilterShow() {
        return filterShow;
    }

    public void setFilterShow(boolean filterShow) {
        this.filterShow = filterShow;
    }

    public boolean isFilterOn() {
        return filterOn;
    }

    public void setFilterOn(boolean filterOn) {
        this.filterOn = filterOn;
    }
}
