package ru.testing.client.common.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Class read application properties
 */
public class AppProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppProperties.class);
    private static final String PROP_FILE = "app.properties";
    private static AppProperties properties;
    private Double version;
    private Double dbVersion;
    private String tagsUrl;
    private String lastReleaseUrl;
    private String aboutUrl;
    private String jsonPrettyReplaceRegex;

    private AppProperties() {
        Properties properties = new Properties();
        try {
            properties.load(AppProperties.class.getClassLoader().getResourceAsStream(PROP_FILE));
            setVersion(Double.parseDouble(properties.getProperty("version")));
            setDbVersion(Double.parseDouble(properties.getProperty("db.version")));
            setTagsUrl(properties.getProperty("tags.url"));
            setLastReleaseUrl(properties.getProperty("last.release.url"));
            setAboutUrl(properties.getProperty("about.url"));
            setJsonPrettyReplaceRegex(properties.getProperty("json.pretty.replace.regex"));
        } catch (IOException e) {
            LOGGER.error("Error load properties: {}", e.getMessage());
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

    public Double getVersion() {
        return version;
    }

    private void setVersion(Double version) {
        this.version = version;
    }

    public Double getDbVersion() {
        return dbVersion;
    }

    private void setDbVersion(Double dbVersion) {
        this.dbVersion = dbVersion;
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

    public String getJsonPrettyReplaceRegex() {
        return jsonPrettyReplaceRegex;
    }

    private void setJsonPrettyReplaceRegex(String jsonPrettyReplaceRegex) {
        this.jsonPrettyReplaceRegex = jsonPrettyReplaceRegex;
    }
}
