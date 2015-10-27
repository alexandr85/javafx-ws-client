package ru.testing.client.gui.tools;

import javafx.application.Platform;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FilesOperations {

    private static final String TEXT_FILE_NAME = "output.txt";

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
            Platform.runLater(() -> Dialogs.getExceptionDialog(io));
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    Dialogs.getInfoDialog("Save successful");
                } catch (Exception e) {
                    Platform.runLater(() -> Dialogs.getExceptionDialog(e));
                }
            }
        }
    }
}
