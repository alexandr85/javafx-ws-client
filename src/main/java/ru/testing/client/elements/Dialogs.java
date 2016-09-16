package ru.testing.client.elements;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.MainApp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * Class contains javafx modal dialogs
 * Examples @link {http://code.makery.ch/blog/javafx-dialogs-official/}
 */
public class Dialogs {

    private static final Logger LOGGER = LoggerFactory.getLogger(Dialogs.class);
    private static final double DIALOG_WEIGHT = 420;

    /**
     * Show info message
     *
     * @param info String message
     */
    public void getInfoDialog(String info) {
        Alert alert = new Alert(INFORMATION);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }

    /**
     * Show error dialog with exception message
     */
    public void getExceptionDialog(Exception e) {
        try {
            Alert alert = new Alert(ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
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
            alert.initOwner(MainApp.getPrimaryStage());
            alert.showAndWait();
        } catch (IllegalStateException se) {
            LOGGER.error("Error show exception dialog message: {}", se.getLocalizedMessage());
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
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setWidth(DIALOG_WEIGHT);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show warning message dialog
     *
     * @param message String
     */
    public void getWarningDialog(String message) {
        Alert alert = new Alert(WARNING);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(MainApp.getPrimaryStage());
        alert.showAndWait();
    }
}
