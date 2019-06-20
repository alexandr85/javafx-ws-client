package ru.testing.client.common.properties;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Class read application properties
 */
public class AppProperties {

    private static final Logger LOGGER = Logger.getLogger(AppProperties.class);
    private static final String PROP_FILE = "app.properties";
    private static AppProperties properties;
    private Settings defaultSettings;
    private String version;
    private String homeFolder;
    private String tagsUrl;
    private String lastReleaseUrl;
    private String aboutUrl;

    private AppProperties() {
        var props = new Properties();
        var is = AppProperties.class.getClassLoader().getResourceAsStream(PROP_FILE);

        try {
            if (is != null) {
                props.load(is);
            } else {
                throw new IOException("Not found app properties");
            }
        } catch (IOException ioe) {
            LOGGER.error(ioe);
            System.exit(1);
        }

        // read main application properties
        version = props.getProperty("version");
        homeFolder = props.getProperty("home.folder");

        // read github properties
        tagsUrl = props.getProperty("tags.url");
        lastReleaseUrl = props.getProperty("last.release.url");
        aboutUrl = props.getProperty("about.url");

        // read default settings
        defaultSettings = new Settings(
                Integer.valueOf(props.getProperty("settings.msg.font.size")),
                Boolean.valueOf(props.getProperty("settings.msg.wrap")),
                Boolean.valueOf(props.getProperty("settings.ws.auto.scroll")),
                Boolean.valueOf(props.getProperty("settings.ws.ssl.validate")),
                Boolean.valueOf(props.getProperty("settings.ws.compression"))
        );
    }

    /**
     * Get application properties
     *
     * @return AppProperties
     */
    public static AppProperties getInstance() {
        if (properties == null) {
            properties = new AppProperties();
        }
        return properties;
    }

    public Settings getSettings() {
        return getSettings(false);
    }

    /**
     * Get default settings or saved settings
     *
     * @param isDefault boolean load only default
     * @return Settings
     */
    public Settings getSettings(boolean isDefault) {
        if (isDefault) {
            return defaultSettings;
        }

        var gson = new Gson();
        var path = Settings.getSettingsPath();

        if (!path.toFile().exists()) {
            LOGGER.debug(String.format("Can't read settings from %s. Load default settings", path.toString()));
            defaultSettings.save();
            return defaultSettings;
        }

        try {

            var reader = new JsonReader(new FileReader(path.toFile()));
            return gson.fromJson(reader, Settings.class);
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException e) {
            LOGGER.warn(String.format("Can't read settings from %s. Load default settings", path.toString()));
            return defaultSettings;
        }
    }

    public String getVersion() {
        return version;
    }

    String getHomeFolder() {
        return homeFolder;
    }

    public String getTagsUrl() {
        return tagsUrl;
    }

    public String getLastReleaseUrl() {
        return lastReleaseUrl;
    }

    public String getAboutUrl() {
        return aboutUrl;
    }
}
