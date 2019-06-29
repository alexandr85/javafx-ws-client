package ru.testing.client;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.testing.client.common.properties.AppProperties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test application properties data
 */
public class PropertiesTest {

    private static final String URLS_START = "https://github.com";
    private static AppProperties properties;

    @BeforeClass
    public static void testClassSetup() {
        properties = AppProperties.getInstance();
    }

    @Test
    public void testVersionValue() {
        String version = properties.getVersion();
        assertThat("Version type", version, instanceOf(String.class));
        assertThat("Version value", Double.parseDouble(version), greaterThan(1.0));
    }

    @Test
    public void testTagsUrlValue() {
        String tagsUrl = properties.getTagsUrl();
        assertThat("Tags url type", tagsUrl, instanceOf(String.class));
        assertThat("Tags url value", tagsUrl, startsWith("https://api.github.com/"));
    }

    @Test
    public void testLastReleaseUrlValue() {
        String lastReleaseUrl = properties.getLastReleaseUrl();
        assertThat("Last release url type", lastReleaseUrl, instanceOf(String.class));
        assertThat("Last release url value", lastReleaseUrl, startsWith(URLS_START));
    }

    @Test
    public void testAboutUrlValue() {
        String aboutUrl = properties.getAboutUrl();
        assertThat("About url type", aboutUrl, instanceOf(String.class));
        assertThat("About url value", aboutUrl, startsWith(URLS_START));
    }
}
