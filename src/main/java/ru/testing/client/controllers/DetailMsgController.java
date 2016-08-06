package ru.testing.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.elements.message.OutputMessage;

/**
 * Controller for detail message tab form
 */
public class DetailMsgController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetailMsgController.class.getName());
    private MainController main;
    private OutputMessage message;

    @FXML
    private TextArea msgTextArea;
    @FXML
    private ToggleButton wrapTButton;
    @FXML
    private ToggleButton jsonPrettyButton;
    @FXML
    private Label msgTimeLabel;
    @FXML
    private Label msgLengthLabel;

    public DetailMsgController(OutputMessage item, MainController mainController) {
        message = item;
        main = mainController;
    }

    @FXML
    private void initialize() {

        // Set message text and data on init tab
        msgTextArea.setText(message.getMessage());
        msgTimeLabel.setText(String.format("Time: %s", message.getFormattedTime()));
        msgLengthLabel.setText(String.format("Length: %s", message.getMessage().length()));

        // Set message as json pretty or text
        jsonPrettyButton.setOnAction(event -> {
            if (jsonPrettyButton.isSelected()){
                msgTextArea.setText(getJsonPretty(message.getMessage()));
            } else {
                msgTextArea.setText(message.getMessage());
            }
        });

        // Set text area wrap or not
        wrapTButton.setOnAction(event -> {
            if (wrapTButton.isSelected()) {
                msgTextArea.setWrapText(true);
            } else {
                msgTextArea.setWrapText(false);
            }
        });
    }

    /**
     * Try pretty json string from cell message
     *
     * @param message String
     * @return String
     */
    private String getJsonPretty(String message) {
        try {
            String json = message.replaceAll(main.getProperties().getJsonPrettyReplaceRegex(), "");
            ObjectMapper mapper = new ObjectMapper();
            Object object = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error("Error pretty json from string: {}", e.getMessage());
            jsonPrettyButton.setSelected(false);
            return message;
        }
    }
}
