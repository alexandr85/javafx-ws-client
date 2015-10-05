package ru.testing.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.websocket.Client;

import java.net.URI;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final String DEFAULT_WS_SERVER_URL = "ws://echo.websocket.org";
    private static final String CONNECT_STATUS = "#5CB85C";
    private static final String DISCONNECT_STATUS = "#D9534F";
    private static final int HISTORY_STAGE_WIDTH = 400;
    private static final int HISTORY_STAGE_HEIGHT = 260;
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 200;
    private static Client client;
    protected final ObservableList<SendMessage> sendMessageList = FXCollections.observableArrayList();
    private boolean connectionStatus;
    protected Stage history;

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
    private TextField filterText;

    @FXML
    private Button addFilterBtn;

    @FXML
    private MenuButton filterList;

    @FXML
    protected TextField messageText;

    @FXML
    private Button messageSendBtn;

    @FXML
    protected Button messageSendHistoryBtn;

    @FXML
    HistoryController historyController;

    /**
     * Method run then controller initialize
     */
    @FXML
    private void initialize() {

        // Set default websocket server url for test
        serverUrl.setText(DEFAULT_WS_SERVER_URL);

        // Clean output text area action
        cleanOutputTextBtn.setOnAction(((event) -> {
            if (!outputText.getText().isEmpty()) {
                outputText.clear();
            }
        }));

        // Connect or disconnect with websocket server
        connectBtn.setOnAction((event -> actionConnectDisconnect()));
        serverUrl.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                actionConnectDisconnect();
            }
        });

        // Send message
        messageSendBtn.setOnAction((event -> sendWebsocketMessage()));
        messageText.setOnKeyPressed((keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendWebsocketMessage();
            }
        });

        // Add filter
        addFilterBtn.setOnMouseClicked((event -> addToFilterList()));
        filterText.setOnKeyPressed((keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addToFilterList();
            }
        }));

        // Show send message history window
        messageSendHistoryBtn.setOnAction((event -> {
            if (history == null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/history.fxml"));
                    loader.setController(new HistoryController());
                    historyController = loader.getController();
                    historyController.init(this);
                    Pane root = loader.load();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().addAll(getClass().getResource("/styles/history.css").toExternalForm());
                    history = new Stage();
                    history.setAlwaysOnTop(true);
                    history.initStyle(StageStyle.UNDECORATED);
                    history.setTitle("Send messages history");
                    history.setMinWidth(HISTORY_STAGE_WIDTH);
                    history.setMinHeight(HISTORY_STAGE_HEIGHT);
                    history.setScene(scene);
                    history.setResizable(false);
                    history.setOnCloseRequest((eventHistory -> history = null));
                    history.show();
                } catch (Exception e) {
                    Dialogs.getExceptionDialog(e);
                }
            } else {
                history.show();
                history.requestFocus();
            }
        }));
    }

    /**
     * Connected to websocket server if connectionStatus = false
     * or disconnect from websocket server if connectionStatus = true
     */
    private void actionConnectDisconnect() {
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
            connectionStatus = true;
            checkConnectionStatus();
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
                status.setFill(Paint.valueOf(CONNECT_STATUS));
                connectBtn.setText("Disconnect");
                messageText.setDisable(false);
                messageSendBtn.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                status.setFill(Paint.valueOf(DISCONNECT_STATUS));
                connectBtn.setText("Connect");
                messageText.setDisable(true);
                messageSendBtn.setDisable(true);
                connectionStatus = false;
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
                    while (connectionStatus) {
                        if (client != null && client.getSession() != null && client.getSession().isOpen()) {
                            setConnectStatus(true);
                        } else {
                            setConnectStatus(false);
                        }
                        Thread.sleep(CHECK_CONNECTION_STATUS_TIMEOUT);
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
                filterText.requestFocus();
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
        if (!messageText.getText().isEmpty()) {
            sendMessageList.add(new SendMessage(messageText.getText()));
            if (client != null) {
                client.sendMessage(messageText.getText());
            }
            messageText.clear();
            messageText.requestFocus();
        }
        if (sendMessageList.size() > 0) {
            messageSendHistoryBtn.setDisable(false);
        }
    }
}
