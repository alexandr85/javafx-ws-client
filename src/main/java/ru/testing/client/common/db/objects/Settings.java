package ru.testing.client.common.db.objects;

/**
 * Global app settings
 */
public class Settings {

    private int fontSize;
    private boolean textWrap;
    private boolean jsonPretty;
    private boolean autoScroll;

    public Settings(int fontSize, boolean textWrap, boolean jsonPretty, boolean autoScroll) {
        this.fontSize = fontSize;
        this.textWrap = textWrap;
        this.jsonPretty = jsonPretty;
        this.autoScroll = autoScroll;
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

    public boolean isAutoScroll() {
        return autoScroll;
    }
}
