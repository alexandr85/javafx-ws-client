package ru.testing.client;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.qatools.properties.PropertyLoader;
import ru.testing.client.common.AppProperties;
import ru.testing.client.common.github.GitHub;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test git hub api data
 */
public class GitHubTest {

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

    @Test
    public void testTagVersion() {
        GitHub git = new GitHub(properties);
        Double version = properties.getVertion();
        assertThat("Version value", git.getLastVersion(), both(lessThanOrEqualTo(version)).and(greaterThan(1.0)));
    }
}
