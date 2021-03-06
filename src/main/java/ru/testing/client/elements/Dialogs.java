package ru.testing.client.elements;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.log4j.Logger;
import ru.testing.client.FXApp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * Class contains javafx modal dialogs
 * Examples @link {http://code.makery.ch/blog/javafx-dialogs-official/}
 */
public class Dialogs {

    private static final Logger LOGGER = Logger.getLogger(Dialogs.class);
    private static final double DIALOG_WEIGHT = 420;

    /**
     * Show info message
     *
     * @param info String message
     */
    public void getInfoDialog(String info) {
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.initOwner(FXApp.getPrimaryStage());
        alert.showAndWait();
    }

    /**
     * Show error dialog with exception message
     */
    public void getExceptionDialog(Exception e) {
        try {
            Alert alert = new Alert(ERROR);
            alert.setTitle("Oops! Catch some error");
            alert.setHeaderText(null);
            if (e.getLocalizedMessage() != null && !e.getLocalizedMessage().isEmpty()) {
                alert.setContentText(e.getLocalizedMessage());
            } else {
                alert.setContentText(e.getMessage());
            }

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
            alert.initOwner(FXApp.getPrimaryStage());
            alert.showAndWait();
        } catch (IllegalStateException se) {
            LOGGER.error(String.format("Error show exception dialog message: %s", se.getLocalizedMessage()));
        }
    }

    /**
     * Show confirmation dialog
     *
     * @param message String
     * @return boolean
     */
    public boolean getConfirmationDialog(String title, String message) {
        Alert alert = new Alert(CONFIRMATION);
        alert.setWidth(DIALOG_WEIGHT);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(FXApp.getPrimaryStage());
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show warning message
     *
     * @param message String
     */
    public void getWarningDialog(String message) {
        Alert alert = new Alert(WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(FXApp.getPrimaryStage());
        alert.showAndWait();
    }
}
