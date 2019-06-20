package ru.testing.client.common;

import javafx.application.Platform;
import org.apache.log4j.Logger;
import ru.testing.client.MainApp;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.Dialogs;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Tools for file operations
 */
public class FilesOperations {

    private static final Logger LOGGER = Logger.getLogger(FilesOperations.class);
    private static final String TEXT_FILE_NAME = String.format("%s/%s", System.getProperty("user.home"), "output.log");
    private MainController mainController = MainApp.getMainController();

    /**
     * Save text to file
     *
     * @param text String
     */
    public void saveTextToFile(String text) {
        var dialogs = new Dialogs();
        mainController.setProgressVisible(true);
        if (text == null || text.isEmpty()) {
            Platform.runLater(() -> dialogs.getWarningDialog("Nothing for save"));
            mainController.setProgressVisible(false);
            return;
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(TEXT_FILE_NAME), StandardCharsets.UTF_8)
            );
            writer.write(text);
        } catch (IOException io) {
            dialogs.getExceptionDialog(io);
            LOGGER.error("Can't save file", io);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    dialogs.getInfoDialog(String.format("Save successful in file %s", TEXT_FILE_NAME));
                } catch (Exception e) {
                    dialogs.getExceptionDialog(e);
                    LOGGER.error("Can't close writer", e);
                }
            }
            mainController.setProgressVisible(false);
        }
    }
}
