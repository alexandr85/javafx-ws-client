package ru.testing.client.common.properties;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class read application properties
 */
public class AppProperties {

    private static final Logger LOGGER = Logger.getLogger(AppProperties.class);
    private static final String PROP_FILE = "app.properties";
    private static AppProperties properties;
    private String version;
    private String dbVersion;
    private String homeFolder;
    private String tagsUrl;
    private String lastReleaseUrl;
    private String aboutUrl;

    private AppProperties() {
        Properties properties = new Properties();
        try {

            InputStream is = AppProperties.class.getClassLoader().getResourceAsStream(PROP_FILE);

            if (is != null) {
                properties.load(is);
            } else {
                throw new IOException("Not found app properties");
            }

            setVersion(properties.getProperty("version"));
            setDbVersion(properties.getProperty("db.version"));
            setHomeFolder(properties.getProperty("home.folder"));
            setTagsUrl(properties.getProperty("tags.url"));
            setLastReleaseUrl(properties.getProperty("last.release.url"));
            setAboutUrl(properties.getProperty("about.url"));
        } catch (IOException e) {
            LOGGER.error("Error load properties", e);
        }
    }

    /**
     * Get application properties
     * @return AppProperties
     */
    public static AppProperties getAppProperties() {
        if (properties == null) {
            properties = new AppProperties();
        }
        return properties;
    }

    public String getVersion() {
        return version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    private void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getHomeFolder() {
        return homeFolder;
    }

    private void setHomeFolder(String homeFolder) {
        this.homeFolder = homeFolder;
    }

    public String getTagsUrl() {
        return tagsUrl;
    }

    private void setTagsUrl(String tagsUrl) {
        this.tagsUrl = tagsUrl;
    }

    public String getLastReleaseUrl() {
        return lastReleaseUrl;
    }

    private void setLastReleaseUrl(String lastReleaseUrl) {
        this.lastReleaseUrl = lastReleaseUrl;
    }

    public String getAboutUrl() {
        return aboutUrl;
    }

    private void setAboutUrl(String aboutUrl) {
        this.aboutUrl = aboutUrl;
    }
}
