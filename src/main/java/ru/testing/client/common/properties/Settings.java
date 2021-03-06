package ru.testing.client.common.properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Global app settings
 */
public class Settings {

    private static final Logger LOGGER = Logger.getLogger(Settings.class);
    private static final String SETTINGS_FILE_NAME = "settings.json";

    @SerializedName("font")
    private int fontSize;

    @SerializedName("wrap")
    private boolean textWrap;

    @SerializedName("scroll")
    private boolean autoScroll;

    @SerializedName("ssl")
    private boolean wsSslValidate;

    @SerializedName("compress")
    private boolean withCompression;

    @SerializedName("check_update")
    private boolean checkUpdate;

    public Settings(int fs, boolean tw, boolean as, boolean wss, boolean wc, boolean cu) {
        fontSize = fs;
        textWrap = tw;
        autoScroll = as;
        wsSslValidate = wss;
        withCompression = wc;
        checkUpdate = cu;
    }

    static Path getSettingsPath() {
        AppProperties props = AppProperties.getInstance();
        String userHomeFolder = System.getProperty("user.home");
        return Paths.get(userHomeFolder, String.format(".%s", props.getAppName()), SETTINGS_FILE_NAME);
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

    public boolean isCheckUpdate() {
        return checkUpdate;
    }

    public boolean save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path path = getSettingsPath();

        try {
            FileWriter writer = new FileWriter(path.toFile());
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            LOGGER.error(String.format("Can't save settings to %s", path.toString()), ioe);
            return false;
        }

        LOGGER.debug(String.format("New settings success saved to %s", path.toString()));
        return true;
    }
}
