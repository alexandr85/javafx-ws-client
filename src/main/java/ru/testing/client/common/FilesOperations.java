package ru.testing.client.common;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.elements.sessions.Sessions;
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
    private static final String SESSIONS_FILE = "sessions.xml";
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
    public void saveSessionsData(Sessions jaxbElement) {
        try {
            JAXBContext context = JAXBContext.newInstance(Sessions.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jaxbElement, new File(SESSIONS_FILE));
        } catch (JAXBException e) {
            LOGGER.error(e.getCause().getMessage());
        }
    }

    /**
     * Load profile data from xml file
     * @return Session
     */
    public Sessions readSessionsData() {
        File file = new File(SESSIONS_FILE);
        boolean canRead = false;
        try {
            if (!file.exists()) {
                canRead = file.createNewFile();
            }
            canRead = file.canRead();
        } catch (IOException io) {
            LOGGER.error(io.getMessage());
        }
        if (canRead) {
            try {
                JAXBContext context = JAXBContext.newInstance(Sessions.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return  (Sessions) unmarshaller.unmarshal(file);
            } catch (JAXBException e) {
                LOGGER.error(e.getCause().getMessage());
                return null;
            }
        } else {
            LOGGER.error("Can't read {} file", SESSIONS_FILE);
            return null;
        }
    }



    /**
     * Get sessions file name
     * @return String
     */
    public static String getSessionsFile() {
        return SESSIONS_FILE;
    }
}
