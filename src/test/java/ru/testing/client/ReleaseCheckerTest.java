package ru.testing.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.testing.client.common.properties.AppProperties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test git hub api data
 */
public class ReleaseCheckerTest {

    private static AppProperties properties;

    @BeforeAll
    public static void testClassSetup() {
        properties = AppProperties.getAppProperties();
    }

    @Test
    public void testGitHubUrl() {
        String url = properties.getTagsUrl();
        assertNotNull(url);
        assertTrue(url.startsWith("https://"));
    }
}
