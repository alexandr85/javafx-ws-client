package ru.testing.client.gui;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private Button filterBtn;

    @FXML
    private TextField filterValue;

    @FXML
    private Label filterSave;

    @FXML
    private TextField messageText;

    @FXML
    private Button messageSendBtn;

    private boolean connectionStatus;
    private String filterString;

    /**
     * Method run then controller initialize
     */
    @FXML
    private void initialize() {
        checkConnectionStatus();
        outputText.setFocusTraversable(false);

        // Clean output text area action
        cleanOutputTextBtn.setOnAction(((event) -> {
            if (!outputText.getText().isEmpty()) {
                outputText.clear();
                LOGGER.debug("Clean all response messages");
            }
        }));

        // Connect or disconnect with websocket server
        connectBtn.setOnAction((event -> connectedToServer()));
        serverUrl.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                connectedToServer();
            }
        });

        // Add filter value
        filterBtn.setOnAction((event -> applyFilter()));

        filterValue.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                applyFilter();
            }
        }));

        // Add action for clear filter
        filterSave.setOnMouseClicked((event -> {
            if (!filterSave.getText().isEmpty() && Dialogs.getConfirmationDialog("Do you want to clear filter?")) {
                filterString = "";
                filterSave.setText(filterString);
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
                if (filterString == null || filterString.isEmpty()) {
                    outputText.appendText(String.format("%s\n", message));
                } else if (message.contains(filterString)){
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
    private void applyFilter() {
        if (filterValue != null) {
            filterString = filterValue.getText();
            filterSave.setText(filterString);
            LOGGER.debug("Use filter string for new message: {}", filterString);
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
