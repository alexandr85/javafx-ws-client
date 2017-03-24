package ru.testing.client.controllers;

import com.google.gson.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.ReceivedMessage;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.elements.message.ReceivedMessageType;

/**
 * Controller for detail message tab form
 */
public class TabDetailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabDetailController.class.getName());
    private DataBase dataBase = DataBase.getInstance();
    private ReceivedMessage message;

    @FXML
    private TextArea txMsgArea;
    @FXML
    private ToggleButton bWrapText;
    @FXML
    private ToggleButton bPrettyJson;
    @FXML
    private Label msgTimeLabel;
    @FXML
    private Label msgLengthLabel;

    @FXML
    private void initialize() {

        // Get message settings
        Settings settings = dataBase.getSettings();

        // Set message as json pretty or text
        bPrettyJson.setOnAction(event -> {
            if (bPrettyJson.isSelected()) {
                txMsgArea.setText(getJsonPretty(message.getMessage()));
            } else {
                txMsgArea.setText(message.getMessage());
            }
        });
        if (settings.isJsonPretty()) {
            bPrettyJson.fire();
        }

        // Set text area wrap or not
        bWrapText.setOnAction(event -> {
            if (bWrapText.isSelected()) {
                txMsgArea.setWrapText(true);
            } else {
                txMsgArea.setWrapText(false);
            }
        });
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        // Set message font size
        txMsgArea.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));
    }

    /**
     * Set message data in tab
     *
     * @param message ReceivedMessage
     */
    public void setMessage(ReceivedMessage message) {
        this.message = message;

        // Set message text and data on init tab
        txMsgArea.setText(message.getMessage());
        String sb = (message.getMessageType() == ReceivedMessageType.RECEIVED ? "Received " : "Send ") +
                "time: " + message.getFormattedTime();
        msgTimeLabel.setText(sb);
        msgLengthLabel.setText(String.format("Length: %s", message.getMessage().length()));
    }

    /**
     * Try pretty json string from cell message
     *
     * @param message String
     * @return String
     */
    private String getJsonPretty(String message) {
        try {
            String json = message.replaceAll(dataBase.getSettings().getJsonRegex(), "");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(json);
            return gson.toJson(jsonElement);
        } catch (JsonIOException e) {
            LOGGER.error("Error pretty json from string: {}", e.getMessage());
            bPrettyJson.setSelected(false);
            return message;
        }
    }
}
