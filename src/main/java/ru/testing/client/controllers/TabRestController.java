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
import javafx.scene.control.TreeView;
import org.apache.log4j.Logger;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.SegmentedButton;
import ru.testing.client.MainApp;
import ru.testing.client.common.DataBase;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.common.objects.JsonView;
import ru.testing.client.common.objects.Settings;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller for detail message tab form
 */
public class TabRestController {

    private static final Logger LOGGER = Logger.getLogger(TabRestController.class);
    private static final int TIMEOUT = 10000;

    private DataBase dataBase = DataBase.getInstance();
    private MainController mainController = MainApp.getMainController();
    private List<Header> headers = new ArrayList<>();
    private List<HttpParameter> parameters = new ArrayList<>();
    private String serverUrl;
    private HttpTypes httpType;
    private String message;

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
        Settings settings = dataBase.getSettings();
        segmentedButton.setToggleGroup(null);

        // Set message as json pretty or text
        bJsonPretty.setOnAction(event -> toggleJsonPrettyMessage(bJsonPretty.isSelected()));

        // Set text area wrap or not
        bWrapText.setOnAction(event -> toggleWrapText(bWrapText.isSelected()));
        if (settings.isTextWrap()) {
            bWrapText.fire();
        }

        jsonView.setCellFactory(JsonView::cellFactory);

        // Hide or show response headers
        showHeaders.setOnAction(event -> toggleShowHeaders(showHeaders.isSelected()));

        // Set message font size
        masterDetailPane.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));

        // Execute http request
        execute();
    }

    void execute() {
        LOGGER.debug("Initializing http client ...");
        Client client = Client.create();
        serverUrl = mainController.getServerUrl().getText();
        httpType = mainController.getHttpType();
        client.setConnectTimeout(TIMEOUT);
        client.setReadTimeout(TIMEOUT);

        // prepare http headers & parameters
        headers.clear();
        headers.addAll(mainController.getHeadersList());
        parameters.clear();
        parameters.addAll(mainController.getHttpParametersList());
        MultivaluedMap<String, String> parametersMap = new MultivaluedMapImpl();
        parameters.forEach(p -> parametersMap.add(p.getName(), p.getValue()));

        ClientResponse response = null;
        switch (httpType) {
            case HTTP_GET:
                LOGGER.debug("Execute http GET request");
                WebResource.Builder getResource = client.resource(serverUrl)
                        .queryParams(parametersMap).getRequestBuilder();
                headers.forEach(header -> getResource.header(header.getName(), header.getValue()));
                response = getResource.get(ClientResponse.class);
                break;
            case HTTP_POST:
                LOGGER.debug("Execute http POST request");
                WebResource.Builder postResource = client.resource(serverUrl).getRequestBuilder();
                mainController.getHeadersList().forEach(header -> postResource.header(header.getName(), header.getValue()));
                response = postResource.post(ClientResponse.class, parametersMap);
        }

        // Set http action
        if (response != null) {
            LOGGER.debug("Get response");
            message = response.getEntity(String.class);

            // Set body result to master node
            Platform.runLater(() -> {
                if (message != null) {

                    // apply json tree to view
                    JsonView view = new JsonView(message);
                    view.apply(jsonView, bJsonPretty, segmentedButton);

                    masterNode.setText(message);
                    toggleWrapText(bWrapText.isSelected());
                    toggleJsonPrettyMessage(bJsonPretty.isSelected());
                    msgLengthLabel.setText(String.valueOf(message.length()));
                }
            });

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
