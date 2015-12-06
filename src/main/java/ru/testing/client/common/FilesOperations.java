package ru.testing.client.common;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.profile.Profile;
import ru.testing.client.elements.Dialogs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Tools for file operations
 */
public class FilesOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesOperations.class);
    private static final String PROFILE_XML_DATA = "profile.xml";
    private static final String TEXT_FILE_NAME = "logs/output.txt";

    /**
     * Save text to file
     * @param text String
     */
    public void saveTextToFile(String text) {
        if (text == null || text.isEmpty()) {
            Platform.runLater(() -> Dialogs.getWarningDialog("Nothing for save"));
            return;
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(TEXT_FILE_NAME), StandardCharsets.UTF_8));
            writer.write(text);
        }
        catch (IOException io) {
            Dialogs.getExceptionDialog(io);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    Dialogs.getInfoDialog(String.format("Save successful in file %s", TEXT_FILE_NAME));
                } catch (Exception e) {
                    Dialogs.getExceptionDialog(e);
                }
            }
        }
    }

    /**
     * Save profile data to xml file
     * @param jaxbElement Objects
     */
    public void saveProfileData(Profile jaxbElement) {
        try {
            JAXBContext context = JAXBContext.newInstance(Profile.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jaxbElement, new File(PROFILE_XML_DATA));
            Dialogs.getInfoDialog("Profile successful saved");
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Load profile data from xml file
     * @return Profile
     */
    public Profile loadProfileData() {
        Profile profileXml = null;
        File file = new File(PROFILE_XML_DATA);
        if (!file.exists()) {
            Dialogs.getWarningDialog(String.format("File %s not found", PROFILE_XML_DATA));
            return null;
        }
        try {
            JAXBContext context = JAXBContext.newInstance(Profile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            profileXml = (Profile) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
            Dialogs.getExceptionDialog(e);
        }
        return profileXml;
    }
}
