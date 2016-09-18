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
    private int msgFontSize;
    private boolean msgWrap;
    private boolean msgJsonPretty;
    private String msgJsonPrettyReplaceRegex;
    private boolean autoScroll;
    private boolean showFilter;
    private boolean showBar;

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
            setMsgJsonPrettyReplaceRegex(properties.getProperty("msg.json.pretty.replace.regex"));
            setAutoScroll(Boolean.parseBoolean(properties.getProperty("profile.auto.scroll")));
            setShowFilter(Boolean.parseBoolean(properties.getProperty("profile.show.filter")));
            setShowBar(Boolean.parseBoolean(properties.getProperty("profile.show.bar")));
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
                getMsgJsonPrettyReplaceRegex(),
                isAutoScroll(),
                isShowBar(),
                isShowFilter()
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

    public boolean isShowFilter() {
        return showFilter;
    }

    private void setShowFilter(boolean profileShowFilter) {
        this.showFilter = profileShowFilter;
    }

    public boolean isShowBar() {
        return showBar;
    }

    private void setShowBar(boolean showBar) {
        this.showBar = showBar;
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
