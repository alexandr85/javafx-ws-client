package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.JsonView;
import ru.testing.client.websocket.ReceivedMessageType;

/**
 * Controller for detail message tab form
 */
public class TabWsMessageController {

    private AppProperties props = AppProperties.getInstance();

    @FXML
    private TextArea txMsgArea;
    @FXML
    private TreeView<String> jsonView;
    @FXML
    private ToggleButton bWrapText;
    @FXML
    private ToggleButton bJsonPretty;
    @FXML
    private Label msgTimeLabel;
    @FXML
    private Label msgLengthLabel;
    @FXML
    private SegmentedButton segmentedButton;

    @FXML
    private void initialize() {

        // Get message settings
        var settings = props.getSettings();
        segmentedButton.setToggleGroup(null);

        jsonView.setCellFactory(JsonView::cellFactory);
        jsonView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Set message as json pretty or text
        bJsonPretty.setOnAction(event -> toggleJsonPretty(bJsonPretty.isSelected()));

        // Set text area wrap or not
        bWrapText.setOnAction(event -> toggleWrapText(bWrapText.isSelected()));
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        // Set message font size
        txMsgArea.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));
        jsonView.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));
    }

    /**
     * Toggle message as json tree, if available
     *
     * @param state boolean
     */
    private void toggleJsonPretty(boolean state) {
        txMsgArea.setVisible(!state);
        txMsgArea.setManaged(!state);
        bWrapText.setDisable(state);
        jsonView.setVisible(state);
        jsonView.setManaged(state);
        bJsonPretty.setSelected(state);
        segmentedButton.requestFocus();
    }

    /**
     * Toggle wrap message text
     *
     * @param state boolean
     */
    private void toggleWrapText(boolean state) {
        txMsgArea.setWrapText(state);
        segmentedButton.requestFocus();
    }

    /**
     * Set message data for current tab
     *
     * @param m received message
     */
    public void setMessage(ReceivedMessage m) {

        // apply json to view
        var view = new JsonView(m.getMessage());
        view.apply(jsonView, bJsonPretty, segmentedButton);

        // Set message text and data on init tab
        txMsgArea.setText(m.getMessage());
        var sb = String.format(
                "%s time: %s",
                m.getMessageType() == ReceivedMessageType.RECEIVED ? "Received" : "Send",
                m.getFormattedTime()
        );
        msgTimeLabel.setText(sb);
        msgLengthLabel.setText(String.format("Length: %s", m.getMessage().length()));
    }
}
