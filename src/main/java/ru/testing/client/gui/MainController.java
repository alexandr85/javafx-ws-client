package ru.testing.client.gui;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.websocket.Client;

import java.net.URI;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final String CONNECT_STATUS = "#5CB85C";
    private static final String DISCONNECT_STATUS = "#D9534F";
    private static Client client;

    @FXML
    private TextField serverUrl;

    @FXML
    private Button connectBtn;

    @FXML
    private Circle status;

    @FXML
    private TextArea outputText;

    @FXML
    private Button cleanOutputTextBtn;

    @FXML
    private Button addFilterBtn;

    @FXML
    private TextField filterText;

    @FXML
    private TextField messageText;

    @FXML
    private Button messageSendBtn;

    @FXML
    private MenuButton filterList;

    private boolean connectionStatus;

    /**
     * Method run then controller initialize
     */
    @FXML
    private void initialize() {

        // Check connection status
        checkConnectionStatus();

        // Clean output text area action
        cleanOutputTextBtn.setOnAction(((event) -> {
            if (!outputText.getText().isEmpty()) {
                outputText.clear();
            }
        }));

        // Connect or disconnect with websocket server
        connectBtn.setOnAction((event -> connectedToServer()));
        serverUrl.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                connectedToServer();
            }
        });

        // Add filter
        addFilterBtn.setOnMouseClicked((event -> addToFilterList()));
        filterText.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addToFilterList();
            }
        }));
    }

    /**
     * Try connected to websocket server
     */
    private void connectedToServer() {
        if (connectionStatus) {
            try {
                client.getSession().close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                Dialogs.getExceptionDialog(e);
            }
        } else {
            if (!serverUrl.getText().isEmpty()) {
                Platform.runLater(this::startClient);
            }
        }
    }

    /**
     * Start websocket client
     */
    private void startClient() {
        try {
            LOGGER.info("Connecting to {} ...", serverUrl.getText());
            client = new Client(new URI(serverUrl.getText()));
            client.addMessageHandler((message -> {
                if (filterList.getItems().size() != 0) {
                    for (MenuItem item : filterList.getItems()) {
                        if (message.contains(item.getText())) {
                            outputText.appendText(String.format("%s\n", message));
                            break;
                        }
                    }
                } else {
                    outputText.appendText(String.format("%s\n", message));
                }
            }));

            // Send message
            messageSendBtn.setOnAction((event -> sendWebsocketMessage()));
            messageText.setOnKeyPressed((keyEvent) -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    sendWebsocketMessage();
                }
            });
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            Dialogs.getExceptionDialog(e);
        }
    }

    /**
     * Set status disable or enable send message text field and button
     * @param isConnected boolean
     */
    private void setConnectStatus(boolean isConnected) {
        if (isConnected) {
            Platform.runLater(() -> {
                connectionStatus = true;
                status.setFill(Paint.valueOf(CONNECT_STATUS));
                connectBtn.setText("Disconnect");
                messageText.setDisable(false);
                messageSendBtn.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                connectionStatus = false;
                status.setFill(Paint.valueOf(DISCONNECT_STATUS));
                connectBtn.setText("Connect");
                messageText.setDisable(true);
                messageSendBtn.setDisable(true);
            });
        }
    }

    /**
     * Check connection status
     */
    private void checkConnectionStatus() {
        Task task = new Task() {

            @Override
            protected Object call() throws Exception {
                try {
                    while (true) {
                        if (client != null && client.getSession() != null && client.getSession().isOpen()) {
                            setConnectStatus(true);
                        } else {
                            setConnectStatus(false);
                        }
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Apply text filter for new response
     */
    private void addToFilterList() {
        if (filterText != null && !filterText.getText().isEmpty()) {
            String filterString = filterText.getText();
            MenuItem menuItem = new MenuItem(filterString);
            menuItem.setOnAction((event -> {
                filterList.getItems().remove(menuItem);
                if (filterList.getItems().size() == 0) {
                    filterList.setDisable(true);
                }
            }));
            filterList.getItems().addAll(menuItem);
            if (filterList.getItems().size() > 0) {
                filterList.setDisable(false);
            }
            filterText.clear();
        }
    }

    /**
     * Send websocket message
     */
    private void sendWebsocketMessage() {
        client.sendMessage(messageText.getText());
        messageText.clear();
    }
}
