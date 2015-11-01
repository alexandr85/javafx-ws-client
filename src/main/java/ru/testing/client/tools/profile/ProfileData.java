package ru.testing.client.tools.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ru.testing.client.common.objects.Profile;
import ru.testing.client.tools.Dialogs;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * Work with application stat, use save and load profile
 */
public class ProfileData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileData.class);
    private static final String PROFILE_XML_DATA = "profile.xml";

    /**
     * Load application stat from profile xml data
     * @return Profile
     */
    public Profile loadProfile() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            ProfileLoader dataLoader = new ProfileLoader();
            File file = new File(PROFILE_XML_DATA);
            if (!file.exists()) {
                Dialogs.getWarningDialog(String.format("File %s not found", PROFILE_XML_DATA));
                return null;
            }
            parser.parse(file, dataLoader);
            return dataLoader.getProfile();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            LOGGER.error("Error parse profile.xml: {}", e.getMessage());
            Dialogs.getExceptionDialog(e);
            return null;
        }
    }
}
