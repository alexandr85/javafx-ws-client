package ru.testing.client.common.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.objects.Settings;

import java.io.IOException;
import java.util.Properties;

/**
 * Class read default properties
 */
public class DefaultProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProperties.class);
    private static final String PROP_FILE = "default.properties";
    private static DefaultProperties properties;
    private String profileName;
    private String profileWsUrl;
    private int msgFontSize;
    private boolean msgWrap;
    private boolean msgJsonPretty;
    private boolean autoScroll;

    private DefaultProperties() {
        Properties properties = new Properties();
        try {
            properties.load(DefaultProperties.class.getClassLoader().getResourceAsStream(PROP_FILE));

            // Set profile default value
            setProfileName(properties.getProperty("profile.name"));
            setProfileWsUrl(properties.getProperty("profile.ws.url"));

            // Set message default value
            setMsgFontSize(Integer.parseInt(properties.getProperty("msg.font.size")));
            setMsgWrap(Boolean.parseBoolean(properties.getProperty("msg.wrap")));
            setMsgJsonPretty(Boolean.parseBoolean(properties.getProperty("msg.json.pretty")));
            setAutoScroll(Boolean.parseBoolean(properties.getProperty("profile.auto.scroll")));
        } catch (IOException e) {
            LOGGER.error("Error load properties: {}", e.getMessage());
        }
    }

    /**
     * Get default properties
     * @return DefaultProperties
     */
    public static DefaultProperties getInstance() {
        if (properties == null) {
            properties = new DefaultProperties();
        }
        return properties;
    }

    /**
     * Get default message view setting as object
     * @return Settings
     */
    public Settings getMessageSettings() {
        return new Settings(
                getMsgFontSize(),
                isMsgWrap(),
                isMsgJsonPretty(),
                isAutoScroll()
        );
    }

    public String getProfileName() {
        return profileName;
    }

    private void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileWsUrl() {
        return profileWsUrl;
    }

    private void setProfileWsUrl(String profileWsUrl) {
        this.profileWsUrl = profileWsUrl;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    private void setAutoScroll(boolean profileAutoScroll) {
        this.autoScroll = profileAutoScroll;
    }

    public int getMsgFontSize() {
        return msgFontSize;
    }

    private void setMsgFontSize(int msgFontSize) {
        this.msgFontSize = msgFontSize;
    }

    public boolean isMsgWrap() {
        return msgWrap;
    }

    private void setMsgWrap(boolean msgWrap) {
        this.msgWrap = msgWrap;
    }

    public boolean isMsgJsonPretty() {
        return msgJsonPretty;
    }

    private void setMsgJsonPretty(boolean msgJsonPretty) {
        this.msgJsonPretty = msgJsonPretty;
    }
}
