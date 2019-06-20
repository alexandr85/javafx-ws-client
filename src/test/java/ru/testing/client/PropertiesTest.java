package ru.testing.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.testing.client.common.properties.AppProperties;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test application properties data
 */
public class PropertiesTest {

    private static final String URLS_START = "https://github.com";
    private static AppProperties properties;

    @BeforeAll
    public static void testClassSetup() {
        properties = AppProperties.getInstance();
    }

    @Test
    public void testVersionValue() {
        String version = properties.getVersion();
        assertTrue(Double.parseDouble(version) > 1.0);
    }

    @Test
    public void testTagsUrlValue() {
        String tagsUrl = properties.getTagsUrl();
        assertTrue(tagsUrl.startsWith("https://api.github.com/"));
    }

    @Test
    public void testLastReleaseUrlValue() {
        String lastReleaseUrl = properties.getLastReleaseUrl();
        assertTrue(lastReleaseUrl.startsWith(URLS_START));
    }

    @Test
    public void testAboutUrlValue() {
        String aboutUrl = properties.getAboutUrl();
        assertTrue(aboutUrl.startsWith(URLS_START));
    }
}
