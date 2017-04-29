package ru.testing.client.controllers;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.MainApp;
import ru.testing.client.common.DataBase;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.Utils;
import ru.testing.client.common.objects.Settings;

import javax.ws.rs.core.MultivaluedMap;

import static ru.testing.client.common.Utils.getJsonPretty;

/**
 * Controller for detail message tab form
 */
public class TabRestController {

    private static final int TIMEOUT = 30000;
    private DataBase dataBase = DataBase.getInstance();
    private MultivaluedMap<String, String> parameters = new MultivaluedMapImpl();
    private String serverUrl;
    private HttpTypes httpType;
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
            if (message != null && bPrettyJson.isSelected()) {
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

        // Create http client
        MainController mainController = MainApp.getMainController();
        serverUrl = mainController.getServerUrl().getText();
        httpType = mainController.getHttpType();
        ClientResponse response = null;
        Client restClient = Client.create();
        restClient.setConnectTimeout(TIMEOUT);
        restClient.setReadTimeout(TIMEOUT);
        mainController.getHttpParametersList()
                .forEach(parameter -> parameters.add(parameter.getName(), parameter.getValue()));

        switch (httpType) {
            case HTTP_GET:
                WebResource getResource = restClient.resource(serverUrl).queryParams(parameters);
                mainController.getHeadersList().forEach(header -> getResource.header(header.getName(), header.getValue()));
                response = getResource.get(ClientResponse.class);
                break;
            case HTTP_POST:
                WebResource portResource = restClient.resource(serverUrl);
                mainController.getHeadersList().forEach(header -> portResource.header(header.getName(), header.getValue()));
                response = portResource.post(ClientResponse.class, parameters);
                break;
        }

        // Set http action
        if (response != null) {
            message = response.getEntity(String.class);

            // Set body result to master node
            masterNode.setText(message);
            msgLengthLabel.setText(String.valueOf(message.length()));

            // Set headers result to detail node
            setHeadersDetail(response);
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
     * @param response ClientResponse
     */
    private void setHeadersDetail(ClientResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpType.getName())
                .append(" ")
                .append(serverUrl)
                .append(" ")
                .append(response.getStatus())
                .append(" ")
                .append(response.getStatusInfo())
                .append("\n")
                .append(response.getResponseDate())
                .append("\n");
        response.getHeaders().forEach((k, v) -> stringBuilder.append(k)
                .append(": ")
                .append(v)
                .append("\n"));
        detailNode.setText(stringBuilder.toString());
    }
}
