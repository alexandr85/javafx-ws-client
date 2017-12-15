package ru.testing.client.common.objects;

/**
 * Global app settings
 */
public class Settings {

    private int fontSize;
    private boolean textWrap;
    private boolean autoScroll;
    private boolean wsSslValidate;
    private boolean withCompression;

    public Settings(int fontSize, boolean textWrap, boolean autoScroll, boolean wsSslValidate, boolean withCompression) {
        this.fontSize = fontSize;
        this.textWrap = textWrap;
        this.autoScroll = autoScroll;
        this.wsSslValidate = wsSslValidate;
        this.withCompression = withCompression;
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

    public boolean isWsSslValidate() {
        return wsSslValidate;
    }

    public boolean isWithCompression() {
        return withCompression;
    }
}
