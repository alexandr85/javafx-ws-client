package ru.testing.client.common;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.Dialogs;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Tools for file operations
 */
public class FilesOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesOperations.class);
    private static final String TEXT_FILE_NAME = "logs/output.txt";

    /**
     * Save text to file
     *
     * @param text String
     * @param main MainController
     */
    public void saveTextToFile(String text, MainController main) {
        main.setProgressVisible(true);
        if (text == null || text.isEmpty()) {
            Platform.runLater(() -> Dialogs.getWarningDialog("Nothing for save"));
            main.setProgressVisible(false);
            return;
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(TEXT_FILE_NAME), StandardCharsets.UTF_8));
            writer.write(text);
        } catch (IOException io) {
            Dialogs.getExceptionDialog(io);
            LOGGER.error("Can't save file: {}", io.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    Dialogs.getInfoDialog(String.format("Save successful in file %s", TEXT_FILE_NAME));
                } catch (Exception e) {
                    Dialogs.getExceptionDialog(e);
                    LOGGER.error("Can't close writer: {}", e.getMessage());
                }
            }
            main.setProgressVisible(false);
        }
    }
}
