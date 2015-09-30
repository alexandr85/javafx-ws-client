package ru.testing.client.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private static final String DISCONNECT_STATUS = "#ff1f1f81";
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
    private TextField messageText;

    @FXML
    private Button messageSendBtn;

    private boolean connectionStatus;

    /**
     * Method run then controller initialize
     */
    @FXML
    private void initialize() {
        checkConnectionStatus();
        outputText.setFocusTraversable(false);

        // Clean output text area action
        cleanOutputTextBtn.setOnAction(((ActionEvent event) -> {
            if (!outputText.getText().isEmpty()) {
                outputText.clear();
                LOGGER.debug("Clean all response messages");
            }
        }));

        // Connect or disconnect with websocket server
        connectBtn.setOnAction((event -> {
            if (!outputText.getText().isEmpty() && connectionStatus) {
                try {
                    client.getSession().close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    Dialogs.getExceptionDialog(e);
                }
            } else {
                Platform.runLater(this::startClient);
            }
        }));
    }

    /**
     * Start websocket client
     */
    private void startClient() {
        try {
            LOGGER.info("Connecting to {} ...", serverUrl.getText());
            client = new Client(new URI(serverUrl.getText()));
            client.addMessageHandler((message -> outputText.appendText(String.format("%s\n", message))));

            messageSendBtn.setOnAction((event -> {
                client.sendMessage(messageText.getText());
                messageText.clear();
            }));
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
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
