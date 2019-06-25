package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.FXApp;
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
    private MainController mainController = FXApp.getMainController();
    private List<Header> headers = new ArrayList<>();
    private List<HttpParameter> parameters = new ArrayList<>();
    private HttpClient client;
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

        // Initialize http client
        LOGGER.debug("Initializing http client ...");
        client = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT))
                .build();

        execute();
    }

    void execute() {
        serverUrl = mainController.getServerUrl().getText();
        httpType = mainController.getHttpType();

        // prepare http headers
        headers.clear();
        headers.addAll(mainController.getHeadersList());

        HttpResponse response = null;
        try {
            switch (httpType) {
                case HTTP_GET:

                    // prepare get parameters
                    parameters.clear();
                    parameters.addAll(mainController.getHttpParametersList());

                    // create get request builder
                    var getAddr = uriWithParameters(serverUrl, parameters);
                    var getRequest = HttpRequest.newBuilder()
                            .GET()
                            .timeout(Duration.ofSeconds(TIMEOUT))
                            .uri(getAddr);

                    getRequest.header("User-Agent", String.format("%s/%s", props.getAppName(), props.getVersion()));
                    headers.forEach(h -> getRequest.header(h.getName(), h.getValue()));

                    LOGGER.debug(String.format("Execute http GET request to %s", getAddr));
                    response = client.send(getRequest.build(), HttpResponse.BodyHandlers.ofString());
                    break;
                case HTTP_POST:
                    // prepare post body
                    var body = mainController.getPostBody();
                    var bodyPublish = body.isEmpty() ?
                            HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);

                    // create post request builder
                    var postAddr = URI.create(serverUrl);
                    var postRequest = HttpRequest.newBuilder()
                            .POST(bodyPublish)
                            .timeout(Duration.ofSeconds(TIMEOUT))
                            .uri(postAddr);

                    postRequest.header("User-Agent", String.format("%s/%s", props.getAppName(), props.getVersion()));
                    headers.forEach(h -> postRequest.header(h.getName(), h.getValue()));

                    LOGGER.debug(String.format("Execute http POST request to %s with body length %d",
                            postAddr, bodyPublish.contentLength()));
                    response = client.send(postRequest.build(), HttpResponse.BodyHandlers.ofString());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.warn(e);
        }

        // Set http action
        if (response != null) {
            LOGGER.debug(String.format("Successful get response with status %d", response.statusCode()));
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
        var builder = new StringBuilder();
        builder.append(httpType.getName())
                .append(" ")
                .append(serverUrl)
                .append(" ")
                .append(response.headers())
                .append(" ")
                .append(response.statusCode())
                .append("\n")
                .append(response.version())
                .append("\n");

        response.headers().map().forEach((k, v) -> builder.append(k)
                .append(": ")
                .append(v)
                .append("\n"));
        detailNode.setText(builder.toString());
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

    private URI uriWithParameters(String url, List<HttpParameter> parameters) {
        if (parameters == null || parameters.size() == 0) {
            return URI.create(url);
        } else {
            var builder = new StringBuilder(url);
            builder.append("?");
            parameters.forEach(p -> builder.append(String.format("%s=%s&", p.getName(), p.getValue())));
            return URI.create(builder.toString());
        }
    }
}
