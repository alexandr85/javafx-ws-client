package ru.testing.client.common.db.objects;

/**
 * Global app settings
 */
public class Settings {

    private int fontSize;
    private boolean textWrap;
    private boolean jsonPretty;
    private String jsonRegex;
    private boolean autoScroll;
    private boolean barShow;
    private boolean filterShow;

    public Settings(int fontSize, boolean textWrap, boolean jsonPretty, String jsonRegex,
                    boolean autoScroll, boolean barShow, boolean filterShow) {
        this.fontSize = fontSize;
        this.textWrap = textWrap;
        this.jsonPretty = jsonPretty;
        this.jsonRegex = jsonRegex;
        this.autoScroll = autoScroll;
        this.barShow = barShow;
        this.filterShow = filterShow;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isTextWrap() {
        return textWrap;
    }

    public boolean isJsonPretty() {
        return jsonPretty;
    }

    public String getJsonRegex() {
        return jsonRegex;
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
}
