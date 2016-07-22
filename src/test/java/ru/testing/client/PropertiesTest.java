package ru.testing.client;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.testing.client.common.AppProperties;

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
        properties = new AppProperties();
    }

    @Test
    public void testVersionValue() {
        assertThat("Version type", properties.getVersion(), instanceOf(Double.class));
        assertThat("Version value", properties.getVersion(), greaterThan(0.1));
    }

    @Test
    public void testTagsUrlValue() {
        assertThat("Tags url type", properties.getTagsUrl(), instanceOf(String.class));
        assertThat("Tags url value", properties.getTagsUrl(), startsWith("https://api.github.com/"));
    }

    @Test
    public void testLastReleaseUrlValue() {
        assertThat("Last release url type", properties.getLastReleaseUrl(), instanceOf(String.class));
        assertThat("Last release url value", properties.getLastReleaseUrl(), startsWith(URLS_START));
    }

    @Test
    public void testAboutUrlValue() {
        assertThat("About url type", properties.getAboutUrl(), instanceOf(String.class));
        assertThat("About url value", properties.getAboutUrl(), startsWith(URLS_START));
    }
}
