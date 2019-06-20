package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.MainApp;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.JsonView;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller for detail message tab form
 */
public class TabRestController {

    private static final Logger LOGGER = Logger.getLogger(TabRestController.class);
    private static final int TIMEOUT = 10000;

    private AppProperties props = AppProperties.getInstance();
    private MainController mainController = MainApp.getMainController();
    private List<Header> headers = new ArrayList<>();
    private List<HttpParameter> parameters = new ArrayList<>();
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
        var settings = props.getSettings();
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
    }

    void execute() {
        LOGGER.debug("Initializing http client ...");

        var client = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT))
                .build();

        serverUrl = mainController.getServerUrl().getText();
        httpType = mainController.getHttpType();


        // prepare http headers & parameters
        headers.clear();
        headers.addAll(mainController.getHeadersList());
        parameters.clear();
        parameters.addAll(mainController.getHttpParametersList());

        var params = new StringBuilder();
        parameters.forEach(p -> params.append(String.format("%s=%s&", p.getName(), p.getValue())));

        HttpResponse response = null;
        try {
            switch (httpType) {
                case HTTP_GET:

                    // create get request builder
                    var getRequest = HttpRequest.newBuilder()
                            .GET()
                            .timeout(Duration.ofSeconds(TIMEOUT))
                            .uri(URI.create(String.format("%s?%s", serverUrl, params.toString())));

                    headers.forEach(h -> getRequest.header(h.getName(), h.getValue()));

                    LOGGER.debug("Execute http GET request");
                    response = client.send(getRequest.build(), HttpResponse.BodyHandlers.ofString());
                    break;
                case HTTP_POST:

                    // create get request builder
                    var postRequest = HttpRequest.newBuilder()
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .timeout(Duration.ofSeconds(TIMEOUT))
                            .uri(URI.create(String.format("%s?%s", serverUrl, params.toString())));

                    headers.forEach(h -> postRequest.header(h.getName(), h.getValue()));

                    LOGGER.debug("Execute http POST request");
                    response = client.send(postRequest.build(), HttpResponse.BodyHandlers.ofString());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.warn(e);
        }

        // Set http action
        if (response != null) {
            LOGGER.debug("Get response");
            String message = response.body().toString();

            // apply json tree to view
            var view = new JsonView(message);
            view.apply(jsonView, bJsonPretty, segmentedButton);

            masterNode.setText(message);
            toggleWrapText(bWrapText.isSelected());
            toggleJsonPrettyMessage(bJsonPretty.isSelected());
            msgLengthLabel.setText(String.valueOf(message.length()));

            // Set headers result to detail node
            setHeadersDetail(response);
        } else {
            LOGGER.warn("Response is null");
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
    private void setHeadersDetail(HttpResponse response) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(httpType.getName())
                .append(" ")
                .append(serverUrl)
                .append(" ")
                .append(response.headers())
                .append(" ")
                .append(response.statusCode())
                .append("\n")
                .append(response.version())
                .append("\n");

        response.headers().map().forEach((k, v) -> stringBuilder.append(k)
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
