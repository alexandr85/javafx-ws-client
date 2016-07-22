package ru.testing.client.common;

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
    private Double version;
    private String tagsUrl;
    private String lastReleaseUrl;
    private String aboutUrl;

    public AppProperties() {
        Properties properties = new Properties();
        try {
            properties.load(AppProperties.class.getClassLoader().getResourceAsStream(PROP_FILE));
            setVersion(Double.parseDouble(properties.getProperty("version")));
            setTagsUrl(properties.getProperty("tags.url"));
            setLastReleaseUrl(properties.getProperty("last.release.url"));
            setAboutUrl(properties.getProperty("about.url"));
        } catch (IOException e) {
            LOGGER.error("Error load properties: {}", e.getMessage());
        }
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
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
