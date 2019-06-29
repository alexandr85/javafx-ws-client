package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.common.Utils;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.common.properties.Settings;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.websocket.ReceivedMessageType;

import static ru.testing.client.common.Utils.getJsonPretty;

/**
 * Controller for detail message tab form
 */
public class TabWsMessageController {

    private AppProperties props = AppProperties.getInstance();
    private ReceivedMessage message;

    @FXML
    private TextArea txMsgArea;
    @FXML
    private ToggleButton bWrapText;
    @FXML
    private ToggleButton bPrettyJson;
    @FXML
    private ToggleButton editMessage;
    @FXML
    private Label msgTimeLabel;
    @FXML
    private Label msgLengthLabel;
    @FXML
    private SegmentedButton segmentedButton;

    @FXML
    private void initialize() {

        // Get message settings
        Settings settings = props.getSettings();
        segmentedButton.setToggleGroup(null);

        // Set message as json pretty or text
        bPrettyJson.setOnAction(event -> {
            if (bPrettyJson.isSelected()) {
                Utils.PrettyStatus status = getJsonPretty(message.getMessage());
                txMsgArea.setText(status.getMessage());
                bPrettyJson.setSelected(status.getButtonSelect());
            } else {
                txMsgArea.setText(message.getMessage());
            }
            segmentedButton.requestFocus();
        });

        // Set text area wrap or not
        bWrapText.setOnAction(event -> {
            if (bWrapText.isSelected()) {
                txMsgArea.setWrapText(true);
            } else {
                txMsgArea.setWrapText(false);
            }
            segmentedButton.requestFocus();
        });
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        // Set enable or disable edit text message
        editMessage.setOnAction(event -> {
            if (editMessage.isSelected()) {
                txMsgArea.setEditable(true);
            } else {
                txMsgArea.setEditable(false);
            }
            segmentedButton.requestFocus();
        });

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
}
