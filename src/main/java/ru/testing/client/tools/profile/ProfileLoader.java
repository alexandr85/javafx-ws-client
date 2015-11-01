package ru.testing.client.tools.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.testing.client.common.objects.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Sax parser profile data from xml
 */
public class ProfileLoader extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileLoader.class);
    private Profile profile;
    private boolean historyNode;
    private boolean filterNode;
    private List<String> historyItems = new ArrayList<>();
    private List<String> filterItems = new ArrayList<>();

    @Override
    public void startDocument() throws SAXException {
        LOGGER.debug("Starting parse profile data ...");
        profile = new Profile();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case "websocketServer":
                profile.setServerUrl(attributes.getValue("url"));
                break;
            case "sendHistory":
                historyNode = true;
                break;
            case "autoScroll":
                profile.setAutoScroll(Boolean.parseBoolean(attributes.getValue("status")));
                break;
            case "messageFilter": {
                profile.setFilter(Boolean.parseBoolean(attributes.getValue("status")));
                filterNode = true;
                break;
            }
            case "item": {
                if (historyNode) {
                    historyItems.add(attributes.getValue("value"));
                }
                if (filterNode) {
                    filterItems.add(attributes.getValue("value"));
                }
                break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "sendHistory":
                historyNode = false;
                break;
            case "messageFilter":
                filterNode = false;
                break;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        profile.setHistoryItem(historyItems);
        profile.setFilterItem(filterItems);
        LOGGER.debug("Finish parse profile data");
    }

    public Profile getProfile() {
        return profile;
    }
}
