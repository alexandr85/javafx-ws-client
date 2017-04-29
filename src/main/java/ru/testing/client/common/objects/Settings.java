package ru.testing.client.common.objects;

/**
 * Global app settings
 */
public class Settings {

    private int fontSize;
    private boolean textWrap;
    private boolean autoScroll;

    public Settings(int fontSize, boolean textWrap, boolean autoScroll) {
        this.fontSize = fontSize;
        this.textWrap = textWrap;
        this.autoScroll = autoScroll;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isTextWrap() {
        return textWrap;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }
}
