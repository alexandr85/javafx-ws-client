package ru.testing.client;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.properties.DefaultProperties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test application appProperties data
 */
public class PropertiesTest {

    private static final String URLS_START = "https://github.com";
    private static AppProperties appProperties;
    private static DefaultProperties defaultProperties;

    @BeforeClass
    public static void testClassSetup() {
        appProperties = AppProperties.getAppProperties();
        defaultProperties = DefaultProperties.getDefaultProperties();
    }

    @Test
    public void testVersionValue() {
        Double version = appProperties.getVersion();
        assertThat("Version type", version, instanceOf(Double.class));
        assertThat("Version value", version, greaterThan(0.1));
    }

    @Test
    public void testTagsUrlValue() {
        String tagsUrl = appProperties.getTagsUrl();
        assertThat("Tags url type", tagsUrl, instanceOf(String.class));
        assertThat("Tags url value", tagsUrl, startsWith("https://api.github.com/"));
    }

    @Test
    public void testLastReleaseUrlValue() {
        String lastReleasUrl = appProperties.getLastReleaseUrl();
        assertThat("Last release url type", lastReleasUrl, instanceOf(String.class));
        assertThat("Last release url value", lastReleasUrl, startsWith(URLS_START));
    }

    @Test
    public void testAboutUrlValue() {
        String aboutUrl = appProperties.getAboutUrl();
        assertThat("About url type", aboutUrl, instanceOf(String.class));
        assertThat("About url value", aboutUrl, startsWith(URLS_START));
    }

    @Test
    public void testDefaultProfileName() {
        String profileName = defaultProperties.getProfileName();
        assertThat("Default profile name type", profileName, instanceOf(String.class));
        assertThat("Default profile name value", profileName, is("default"));
    }

    @Test
    public void testDefaultWsUrl() {
        String wsUrl = defaultProperties.getProfileWsUrl();
        assertThat("Default websocket url type", wsUrl, instanceOf(String.class));
        assertThat("Default websocket url value", wsUrl, startsWith("ws"));
    }

    @Test
    public void testDefaultAutoScroll() {
        boolean isAutoScroll = defaultProperties.isProfileAutoScroll();
        assertThat("Default auto scroll status type", isAutoScroll, instanceOf(boolean.class));
    }

    @Test
    public void testDefaultShowFilter() {
        boolean isShowFilter = defaultProperties.isProfileShowFilter();
        assertThat("Default show filter status type", isShowFilter, instanceOf(boolean.class));
    }

    @Test
    public void testDefaultShowBar() {
        boolean isShowBar = defaultProperties.isProfileShowBar();
        assertThat("Default show bar status type", isShowBar, instanceOf(boolean.class));
    }

    @Test
    public void testDefaultFontSize() {
        int fontSize = defaultProperties.getMsgFontSize();
        assertThat("Default font size type", fontSize, instanceOf(int.class));
        assertThat("Default font size value", fontSize, both(greaterThanOrEqualTo(10)).and(lessThanOrEqualTo(17)));
    }

    @Test
    public void testDefaultMsgWrap() {
        boolean isMsgWrap = defaultProperties.isMsgWrap();
        assertThat("Default message wrap type", isMsgWrap, instanceOf(boolean.class));
    }

    @Test
    public void testDefaultMsgPretty() {
        boolean isMsgPretty = defaultProperties.isMsgJsonPretty();
        assertThat("Default message json pretty type", isMsgPretty, instanceOf(boolean.class));
    }

    @Test
    public void testDefaultMsgPrettyRegex() {
        String regex = defaultProperties.getMsgJsonPrettyReplaceRegex();
        assertThat("Default message json pretty regex type", regex, instanceOf(String.class));
        assertThat("Default message json pretty regex  value size", regex.length(), greaterThan(0));
    }
}
