package ru.testing.client.common;

import ru.qatools.properties.Property;
import ru.qatools.properties.Resource;

/**
 * Load application properties
 */
@Resource.Classpath("app.properties")
public interface AppProperties {

    @Property("version")
    Double getVersion();

    @Property("tags.url")
    String getTagsUrl();

    @Property("last.tag.release")
    String getLastTagUrl();
}
