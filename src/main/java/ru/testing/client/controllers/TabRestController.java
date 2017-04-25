package ru.testing.client.controllers;

import com.sun.jersey.api.client.ClientResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.MainApp;
import ru.testing.client.common.Utils;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.rest.RestClient;

import static ru.testing.client.common.Utils.getJsonPretty;

/**
 * Controller for detail message tab form
 */
public class TabRestController {

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
    private ToggleButton editBody;
    @FXML
    private Label msgLengthLabel;
    @FXML
    private SegmentedButton segmentedButton;

    @FXML
    private void initialize() {

        // Get message settings
        Settings settings = dataBase.getSettings();
        segmentedButton.setToggleGroup(null);

        // Set message as json pretty or text
        bPrettyJson.setOnAction(event -> {
            if (bPrettyJson.isSelected()) {
                Utils.PrettyStatus status = getJsonPretty(message);
                masterNode.setText(status.getMessage());
                bPrettyJson.setSelected(status.getButtonSelect());
            } else {
                masterNode.setText(message);
            }
            segmentedButton.requestFocus();
        });

        // Set text area wrap or not
        bWrapText.setOnAction(event -> {
            if (bWrapText.isSelected()) {
                masterNode.setWrapText(true);
                detailNode.setWrapText(true);
            } else {
                masterNode.setWrapText(false);
                detailNode.setWrapText(false);
            }
            segmentedButton.requestFocus();
        });
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        // Set enable or disable edit body message
        editBody.setOnAction(event -> {
            if (editBody.isSelected()) {
                masterNode.setEditable(true);
            } else {
                masterNode.setEditable(false);
            }
            segmentedButton.requestFocus();
        });

        // Set message font size
        masterDetailPane.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));

        // Hide or show response headers
        showHeaders.setOnAction(event -> {
            if (showHeaders.isSelected()) {
                masterDetailPane.setShowDetailNode(true);
            } else {
                masterDetailPane.setShowDetailNode(false);
            }
            segmentedButton.requestFocus();
        });

        // Prepare http client
        MainController mainController = MainApp.getMainController();
        String url = mainController.getServerUrl().getText();
        RestClient restClient = new RestClient(url);
        restClient.setHttpType(mainController.getHttpType());
        restClient.setHeaders(mainController.getHeadersList());

        // Set http action
        ClientResponse response = restClient.execute();
        if (response != null) {
            message = response.getEntity(String.class);

            // Set body result to master node
            masterNode.setText(message);
            msgLengthLabel.setText(String.valueOf(message.length()));

            // Set headers result to detail node
            setHeadersDetail(restClient, response);
        }
    }

    TextArea getMasterNode() {
        return masterNode;
    }

    TextArea getDetailNode() {
        return detailNode;
    }

    /**
     * Set headers info from response
     *
     * @param restClient RestClient
     * @param response ClientResponse
     */
    private void setHeadersDetail(RestClient restClient, ClientResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(response.getResponseDate())
                .append("\n")
                .append(restClient.getHttpType().getName())
                .append(" ")
                .append(restClient.getUrl())
                .append(" ")
                .append(response.getStatus())
                .append(" ")
                .append(response.getStatusInfo())
                .append("\n");
        response.getHeaders().forEach((k, v) -> stringBuilder.append(k)
                .append(": ")
                .append(v)
                .append("\n"));
        detailNode.setText(stringBuilder.toString());
    }
}
