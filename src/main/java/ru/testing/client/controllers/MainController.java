package ru.testing.client.controllers;

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
import java.util.Scanner;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final String CONNECT_STATUS = "#5CB85C";
    private static final String DISCONNECT_STATUS = "#ff1f1f81";

    @FXML private TextField serverUrl;
    @FXML private Button connectBtn;
    @FXML private Circle status;
    @FXML private TextArea outputText;
    @FXML private Button cleanOutputTextBtn;

    private boolean connectionStatus;

    @FXML void initialize() {

        status.setFill(Paint.valueOf(DISCONNECT_STATUS));

        // Clean output text area action
        cleanOutputTextBtn.setOnAction(((ActionEvent event) -> {
            if (!outputText.getText().isEmpty()) {
                outputText.clear();
                LOGGER.debug("Clean all response messages");
            }
        }));

        // Connect to websocket server
        if (!connectionStatus) {
            connectBtn.setOnAction((event -> {

            }));
        }
    }

    private void startClient() {
        try {
            final Client client = new Client(new URI(serverUrl.getText()));
            String message;
            client.addMessageHandler(outputText -> LOGGER.info(String.format("=> %s", outputText)));
            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Send message (type 'exit' for close connection): ");
                message = scanner.nextLine();
                if (message.equals("exit")) {
                    client.getSession().close();
                    break;
                }
                client.sendMessage(message);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
