package ru.testing.client.gui;

import javafx.application.Platform;
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

import java.io.IOException;
import java.net.URI;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final String CONNECT_STATUS = "#5CB85C";
    private static final String DISCONNECT_STATUS = "#ff1f1f81";
    private Client client;

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

        status.setFill(Paint.valueOf(DISCONNECT_STATUS));
        outputText.setWrapText(true);

        // Clean output text area action
        cleanOutputTextBtn.setOnAction(((ActionEvent event) -> {
            if (!outputText.getText().isEmpty()) {
                outputText.clear();
                LOGGER.debug("Clean all response messages");
            }
        }));

        // Connect or disconnect with websocket server
        connectBtn.setOnAction((event -> {
            if (connectionStatus) {
                try {
                    client.getSession().close();
                    connectionStatus = false;
                    disableSendMessage(true);
                    status.setFill(Paint.valueOf(DISCONNECT_STATUS));
                    connectBtn.setText("Connect");
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
            client.addMessageHandler((message -> {
                outputText.appendText(String.format("%s\n", message));
            }));

            // set status
            status.setFill(Paint.valueOf(CONNECT_STATUS));
            connectBtn.setText("Disconnect");
            connectionStatus = true;

            // enable send message
            disableSendMessage(false);

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
     * @param disable boolean
     */
    private void disableSendMessage(boolean disable) {
        messageText.setDisable(disable);
        messageSendBtn.setDisable(disable);
    }
}
