package ru.testing.client.controllers;

import com.google.gson.*;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.MasterDetailPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.MainApp;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.rest.RestClient;

/**
 * Controller for detail message tab form
 */
public class TabRestMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabRestMessageController.class.getName());
    private DataBase dataBase = DataBase.getInstance();
    private String message;

    @FXML
    private MasterDetailPane masterDetailPane;
    @FXML
    private TextArea masterNode;
    @FXML
    private TextArea detailNode;
    @FXML
    private ToggleButton bWrapText;
    @FXML
    private ToggleButton bPrettyJson;
    @FXML
    private ToggleButton showHeaders;
    @FXML
    private Label msgLengthLabel;

    @FXML
    private void initialize() {

        // Get message settings
        Settings settings = dataBase.getSettings();

        // Set message as json pretty or text
        bPrettyJson.setOnAction(event -> {
            if (bPrettyJson.isSelected()) {
                masterNode.setText(getJsonPretty(message));
            } else {
                masterNode.setText(message);
            }
        });
        if (settings.isJsonPretty()) {
            bPrettyJson.fire();
        }

        // Set text area wrap or not
        bWrapText.setOnAction(event -> {
            if (bWrapText.isSelected()) {
                masterNode.setWrapText(true);
            } else {
                masterNode.setWrapText(false);
            }
        });
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        // Set message font size
        masterDetailPane.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));

        // Hide/show response headers
        showHeaders.setOnAction(event -> {
            if (showHeaders.isSelected()) {
                masterDetailPane.setShowDetailNode(true);
            } else {
                masterDetailPane.setShowDetailNode(false);
            }
        });

        RestClient restClient = new RestClient();
        MainController mainController = MainApp.getMainController();
        WebResource resource = restClient.getResource(mainController.getServerUrl().getText());
        ClientResponse response = null;
        switch (mainController.getHttpType()) {
            case HTTP_GET:
                response = resource.get(ClientResponse.class);
                break;
            case HTTP_POST:
                response = resource.post(ClientResponse.class);
                break;
        }
        if (response != null) {
            message = response.getEntity(String.class);
            masterNode.setText(message);
            detailNode.setText(response.getStatusInfo().toString());
        }
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
        } catch (JsonIOException | JsonSyntaxException e) {
            LOGGER.error("Error pretty json from string: {}", e.getMessage());
            bPrettyJson.setSelected(false);
            return message;
        }
    }
}
