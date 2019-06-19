package ru.testing.client.controllers;

import com.google.gson.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.common.DataBase;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.common.objects.Settings;
import ru.testing.client.websocket.ReceivedMessageType;

import java.util.List;

/**
 * Controller for detail message tab form
 */
public class TabWsMessageController {

    private static final Logger LOGGER = Logger.getLogger(TabWsMessageController.class);

    @FXML
    private TextArea txMsgArea;
    @FXML
    private TreeView<String> jsonView;
    @FXML
    private ToggleButton bWrapText;
    @FXML
    private ToggleButton bPrettyJson;
    @FXML
    private Label msgTimeLabel;
    @FXML
    private Label msgLengthLabel;
    @FXML
    private SegmentedButton segmentedButton;

    private DataBase dataBase = DataBase.getInstance();

    private void prependString(TreeItem<String> item, String key) {
        String value = item.getValue();
        item.setValue(value == null ? String.format("\"%s\"", key) : String.format("\"%s\": %s", key, value));
    }

    private TreeItem<String> createTree(JsonElement element) {
        if (element.isJsonNull()) {
            return new TreeItem<>("null");
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            return new TreeItem<>(
                    primitive.isString() ? '"' + primitive.getAsString() + '"' : primitive.getAsString()
            );
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            TreeItem<String> item = new TreeItem<>("[ ]");

            array.forEach(c -> {
                TreeItem<String> child = createTree(c);
                item.getChildren().add(child);
            });

            return item;
        } else {
            JsonObject object = element.getAsJsonObject();
            TreeItem<String> item = new TreeItem<>("{ }");

            object.entrySet().forEach(o -> {
                TreeItem<String> child = createTree(o.getValue());
                prependString(child, o.getKey());
                item.getChildren().add(child);
            });

            return item;
        }
    }

    @FXML
    private void initialize() {

        // Get message settings
        Settings settings = dataBase.getSettings();
        segmentedButton.setToggleGroup(null);

        jsonView.setCellFactory(tv -> new TreeCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item);
                }
            }
        });

        // Set message as json pretty or text
        bPrettyJson.setOnAction(event -> {
            if (bPrettyJson.isSelected()) {
                txMsgArea.setVisible(false);
                txMsgArea.setManaged(false);
                bWrapText.setDisable(true);
                jsonView.setVisible(true);
                jsonView.setManaged(true);
                bPrettyJson.setSelected(true);
            } else {
                txMsgArea.setVisible(true);
                txMsgArea.setManaged(true);
                bWrapText.setDisable(false);
                jsonView.setVisible(false);
                jsonView.setManaged(false);
                bPrettyJson.setSelected(false);
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

        // Set message font size
        txMsgArea.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));
    }

    /**
     * Set message data in tab
     *
     * @param m received message
     */
    public void setMessage(ReceivedMessage m) {

        try {
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(m.getMessage());
            TreeItem<String> treeRoot = createTree(root);
            jsonView.setRoot(treeRoot);
            expandJsonTree(jsonView.getRoot());
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Can't parse message as json");
            bPrettyJson.setDisable(true);
            Tooltip tp = new Tooltip("Message is invalid json");
            tp.setShowDelay(new Duration(100));
            segmentedButton.setTooltip(tp);
        }

        // Set message text and data on init tab
        txMsgArea.setText(m.getMessage());
        String sb = String.format(
                "%s time: %s",
                m.getMessageType() == ReceivedMessageType.RECEIVED ? "Received" : "Send",
                m.getFormattedTime()
        );
        msgTimeLabel.setText(sb);
        msgLengthLabel.setText(String.format("Length: %s", m.getMessage().length()));
    }

    private void expandJsonTree(TreeItem<String> item) {
        item.expandedProperty().setValue(true);
        List<TreeItem<String>> items = item.getChildren();
        if (items.size() > 0) {
            items.forEach(this::expandJsonTree);
        }
    }
}
