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

    public Profile(String name, String url, boolean autoScroll, boolean barShow,
                   boolean filterShow, boolean filterOn) {
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

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public boolean isBarShow() {
        return barShow;
    }

    public boolean isFilterShow() {
        return filterShow;
    }

    public boolean isFilterOn() {
        return filterOn;
    }
}
