package ru.testing.client;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.github.ReleaseChecker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test git hub api data
 */
public class ReleaseCheckerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseCheckerTest.class);
    private static AppProperties properties;

    @BeforeClass
    public static void testClassSetup() {
        properties = AppProperties.getAppProperties();
    }

    @Test
    public void testGitHubUrl() {
        String url = properties.getTagsUrl();
        assertThat("Git hub url length", url, not(isEmptyOrNullString()));
        assertThat("Git hub url scheme", url, startsWith("https://"));
    }

    @Test(timeout = 30000)
    public void testTagVersion() {
        ReleaseChecker checker = ReleaseChecker.getInstance();
        checker.start();
        while (checker.isAlive()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                LOGGER.error("Thread interrupted exception: {}", e.getMessage());
            }
        }
        String currentVersion = properties.getVersion();
        assertThat("Current version is old", checker.isCurrentVersionOld(currentVersion, checker.getLastVersion()), is(false));
    }
}
