package ru.testing.client.controllers;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.FXApp;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.Utils;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.properties.Settings;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

import static ru.testing.client.common.Utils.getJsonPretty;

/**
 * Controller for detail message tab form
 */
public class TabRestController {

    private static final int TIMEOUT = 10000;
    private AppProperties props = AppProperties.getInstance();
    private MainController mainController = FXApp.getMainController();
    private List<Header> headers = new ArrayList<>();
    private List<HttpParameter> parameters = new ArrayList<>();
    private Client restClient;
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
        Settings settings = props.getSettings();
        segmentedButton.setToggleGroup(null);

        // Set message as json pretty or text
        bPrettyJson.setOnAction(event -> setMasterMessage());

        // Set text area wrap or not
        bWrapText.setOnAction(event -> setWrapText());
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        // Set enable or disable edit body message
        editBody.setOnAction(event -> setEditBody());

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

        // Init http client
        restClient = Client.create();
        restClient.setConnectTimeout(TIMEOUT);
        restClient.setReadTimeout(TIMEOUT);

        // Http data
        serverUrl = mainController.getServerUrl().getText();
        httpType = mainController.getHttpType();

        execute();
    }

    void execute() {
        headers.clear();
        headers.addAll(mainController.getHeadersList());
        parameters.clear();
        parameters.addAll(mainController.getHttpParametersList());

        MultivaluedMap<String, String> parametersMap = new MultivaluedMapImpl();
        parameters.forEach(p -> parametersMap.add(p.getName(), p.getValue()));

        switch (httpType) {
            case HTTP_GET:
                WebResource.Builder getResource = restClient
                        .resource(serverUrl)
                        .queryParams(parametersMap)
                        .getRequestBuilder();

                headers.forEach(header -> getResource.header(header.getName(), header.getValue()));
                headers.add(new Header("User-Agent", String.format("%s/%s", props.getAppName(), props.getVersion())));

                ClientResponse getResponse = getResource.get(ClientResponse.class);
                setResult(getResponse);
                break;
            case HTTP_POST:

                // TODO FIX body
                WebResource.Builder postResource = restClient.resource(serverUrl).getRequestBuilder();
                mainController.getHeadersList().forEach(header -> postResource.header(header.getName(), header.getValue()));
                ClientResponse postResponse = postResource.post(ClientResponse.class, parametersMap);
                setResult(postResponse);
        }
    }

    /**
     * Save request result
     *
     * @param response ClientResponse
     */
    private void setResult(ClientResponse response) {
        // Set http action
        if (response != null) {
            message = response.getEntity(String.class);

            // Set body result to master node
            Platform.runLater(() -> {
                setWrapText();
                setMasterMessage();
                setEditBody();
                msgLengthLabel.setText(String.valueOf(message.length()));
            });

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

    HttpTypes getHttpType() {
        return httpType;
    }

    String getServerUrl() {
        return serverUrl;
    }

    List<Header> getHeaders() {
        return headers;
    }

    List<HttpParameter> getParameters() {
        return parameters;
    }

    private void setMasterMessage() {
        if (message != null && bPrettyJson.isSelected()) {
            Utils.PrettyStatus status = getJsonPretty(message);
            masterNode.setText(status.getMessage());
            bPrettyJson.setSelected(status.getButtonSelect());
        } else {
            masterNode.setText(message);
        }
        segmentedButton.requestFocus();
    }

    private void setWrapText() {
        if (bWrapText.isSelected()) {
            masterNode.setWrapText(true);
            detailNode.setWrapText(true);
        } else {
            masterNode.setWrapText(false);
            detailNode.setWrapText(false);
        }
        segmentedButton.requestFocus();
    }

    private void setEditBody() {
        if (editBody.isSelected()) {
            masterNode.setEditable(true);
        } else {
            masterNode.setEditable(false);
        }
        segmentedButton.requestFocus();
    }
}
