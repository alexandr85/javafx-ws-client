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
    private int msgFontSize;
    private boolean msgWrap;
    private boolean autoScroll;
    private boolean wsSslValidate;

    private DefaultProperties() {
        Properties properties = new Properties();
        try {
            properties.load(DefaultProperties.class.getClassLoader().getResourceAsStream(PROP_FILE));

            // Set default settings value
            setMsgFontSize(Integer.parseInt(properties.getProperty("msg.font.size")));
            setMsgWrap(Boolean.parseBoolean(properties.getProperty("msg.wrap")));
            setAutoScroll(Boolean.parseBoolean(properties.getProperty("ws.auto.scroll")));
            setWsSslValidate(Boolean.parseBoolean(properties.getProperty("ws.ssl.validate")));
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
    public Settings getDefaultSettings() {
        return new Settings(
                getMsgFontSize(),
                isMsgWrap(),
                isAutoScroll(),
                isWsSslValidate()
        );
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

    public boolean isWsSslValidate() {
        return wsSslValidate;
    }

    public void setWsSslValidate(boolean wsSslValidate) {
        this.wsSslValidate = wsSslValidate;
    }
}
