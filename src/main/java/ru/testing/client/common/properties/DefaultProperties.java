package ru.testing.client.common.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.db.objects.Settings;

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
    private boolean profileAutoScroll;
    private boolean profileFilterOn;
    private boolean profileShowFilter;
    private boolean profileShowBar;
    private int msgFontSize;
    private boolean msgWrap;
    private boolean msgJsonPretty;
    private String msgJsonPrettyReplaceRegex;

    private DefaultProperties() {
        Properties properties = new Properties();
        try {
            properties.load(DefaultProperties.class.getClassLoader().getResourceAsStream(PROP_FILE));

            // Set profile default value
            setProfileName(properties.getProperty("profile.name"));
            setProfileWsUrl(properties.getProperty("profile.ws.url"));
            setProfileAutoScroll(Boolean.parseBoolean(properties.getProperty("profile.auto.scroll")));
            setProfileFilterOn(Boolean.parseBoolean(properties.getProperty("profile.filter.on")));
            setProfileShowFilter(Boolean.parseBoolean(properties.getProperty("profile.show.filter")));
            setProfileShowBar(Boolean.parseBoolean(properties.getProperty("profile.show.bar")));

            // Set message default value
            setMsgFontSize(Integer.parseInt(properties.getProperty("msg.font.size")));
            setMsgWrap(Boolean.parseBoolean(properties.getProperty("msg.wrap")));
            setMsgJsonPretty(Boolean.parseBoolean(properties.getProperty("msg.json.pretty")));
            setMsgJsonPrettyReplaceRegex(properties.getProperty("msg.json.pretty.replace.regex"));
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
                getMsgJsonPrettyReplaceRegex()
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

    public boolean isProfileAutoScroll() {
        return profileAutoScroll;
    }

    private void setProfileAutoScroll(boolean profileAutoScroll) {
        this.profileAutoScroll = profileAutoScroll;
    }

    public boolean isProfileFilterOn() {
        return profileFilterOn;
    }

    private void setProfileFilterOn(boolean profileFilterOn) {
        this.profileFilterOn = profileFilterOn;
    }

    public boolean isProfileShowFilter() {
        return profileShowFilter;
    }

    private void setProfileShowFilter(boolean profileShowFilter) {
        this.profileShowFilter = profileShowFilter;
    }

    public boolean isProfileShowBar() {
        return profileShowBar;
    }

    private void setProfileShowBar(boolean profileShowBar) {
        this.profileShowBar = profileShowBar;
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

    public String getMsgJsonPrettyReplaceRegex() {
        return msgJsonPrettyReplaceRegex;
    }

    private void setMsgJsonPrettyReplaceRegex(String msgJsonPrettyReplaceRegex) {
        this.msgJsonPrettyReplaceRegex = msgJsonPrettyReplaceRegex;
    }
}
