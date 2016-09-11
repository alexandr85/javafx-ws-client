package ru.testing.client.common.db.objects;

/**
 * Global app settings
 */
public class Settings {

    private int fontSize;
    private boolean textWrap;
    private boolean jsonPretty;
    private String jsonRegex;

    public Settings(int fontSize, boolean textWrap, boolean jsonPretty, String jsonRegex) {
        this.fontSize = fontSize;
        this.textWrap = textWrap;
        this.jsonPretty = jsonPretty;
        this.jsonRegex = jsonRegex;
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
}
