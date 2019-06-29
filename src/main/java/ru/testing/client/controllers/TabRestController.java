package ru.testing.client.controllers;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.FXApp;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.properties.Settings;
import ru.testing.client.elements.JsonView;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private MasterDetailPane masterDetailPane;
    @FXML
    private TextArea masterNode;
    @FXML
    private TreeView<String> jsonView;
    @FXML
    private TextArea detailNode;
    @FXML
    private ToggleButton bWrapText;
    @FXML
    private ToggleButton bJsonPretty;
    @FXML
    private ToggleButton showHeaders;
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
        bJsonPretty.setOnAction(event -> toggleJsonPrettyMessage(bJsonPretty.isSelected()));

        // Set text area wrap or not
        bWrapText.setOnAction(event -> toggleWrapText(bWrapText.isSelected()));
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        jsonView.setCellFactory(JsonView::cellFactory);
        jsonView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Hide or show response headers
        showHeaders.setOnAction(event -> toggleShowHeaders(showHeaders.isSelected()));

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

        switch (httpType) {
            case HTTP_GET:

                // prepare parameters
                parameters.clear();
                parameters.addAll(mainController.getHttpParametersList());
                MultivaluedMap<String, String> parametersMap = new MultivaluedMapImpl();
                parameters.forEach(p -> parametersMap.add(p.getName(), p.getValue()));

                WebResource.Builder getResource = restClient
                        .resource(serverUrl)
                        .queryParams(parametersMap)
                        .getRequestBuilder();

                // prepare header
                headers.forEach(header -> getResource.header(header.getName(), header.getValue()));
                getResource.header("User-Agent", String.format("%s/%s", props.getAppName(), props.getVersion()));

                // execute response
                ClientResponse getResponse = getResource.get(ClientResponse.class);
                setResult(getResponse);
                break;
            case HTTP_POST:
                WebResource.Builder postResource = restClient.resource(serverUrl).getRequestBuilder();

                // prepare header
                headers.forEach(header -> postResource.header(header.getName(), header.getValue()));
                postResource.header("User-Agent", String.format("%s/%s", props.getAppName(), props.getVersion()));

                // get body
                HttpSettingsController c = mainController.getHttpSettingsPopOver().getHttpSettingsController();
                String body = c.getBodyTextArea().getText();

                // execute response
                ClientResponse postResponse = postResource.post(ClientResponse.class, body);
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
            String message = response.getEntity(String.class);

            // apply json tree to view
            JsonView view = new JsonView(message);
            view.apply(jsonView, bJsonPretty, segmentedButton);

            masterNode.setText(message);
            toggleWrapText(bWrapText.isSelected());
            toggleJsonPrettyMessage(bJsonPretty.isSelected());
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

    private void toggleJsonPrettyMessage(boolean state) {
        masterNode.setVisible(!state);
        masterNode.setManaged(!state);
        jsonView.setVisible(state);
        jsonView.setManaged(state);
        bJsonPretty.setSelected(state);
        segmentedButton.requestFocus();
    }

    private void toggleShowHeaders(boolean state) {
        masterDetailPane.setShowDetailNode(state);
        segmentedButton.requestFocus();
    }

    private void toggleWrapText(boolean state) {
        masterNode.setWrapText(state);
        detailNode.setWrapText(state);
        segmentedButton.requestFocus();
    }
}
