package ru.testing.client;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.qatools.properties.PropertyLoader;
import ru.testing.client.common.AppProperties;
import ru.testing.client.common.github.GitHub;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test git hub api data
 */
public class GitHubTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubTest.class);
    private static AppProperties properties;

    @BeforeClass
    public static void entryPointBeforeTests() {
        properties = PropertyLoader.newInstance().populate(AppProperties.class);
    }

    @Test
    public void testGitHubUrl() {
        String url = properties.getTagsUrl();
        assertThat("Git hub url length", url, not(isEmptyOrNullString()));
        assertThat("Git hub url scheme", url, startsWith("https://"));
    }

    @Test(timeout = 5000)
    public void testTagVersion() {
        GitHub git = new GitHub(properties);
        while (git.isAlive()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
        Double version = properties.getVertion();
        assertThat("Version value", git.getLastVersion(), both(lessThanOrEqualTo(version)).and(greaterThan(1.0)));
    }
}