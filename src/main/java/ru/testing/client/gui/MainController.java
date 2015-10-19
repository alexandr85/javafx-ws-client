package ru.testing.client.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.websocket.Client;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URI;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 1000;
    private static Client client;
    protected final ObservableList<String> sendMessageList = FXCollections.observableArrayList();
    private boolean connectionStatus;
    private Stage mainStage;
    private Tooltip statusTooltip;
    private PopOver historyPopOver;

    public MainController(Stage mainStage) {
        this.mainStage = mainStage;
    }

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
    protected ToggleButton messageSendHistoryBtn;

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {

        // Set circle tooltip status
        setCircleTooltip("Disconnected");

        // Close application
        mainStage.setOnCloseRequest((event -> {
            if (client != null && client.isOpenConnection()) {
                client.closeConnection();
            }
            Platform.exit();
            System.exit(0);
        }));

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
            if (messageSendHistoryBtn.isSelected()) {
                if (historyPopOver == null) {
                    getHistoryPopOver();
                }
                historyPopOver.show(messageSendHistoryBtn, -1);
            } else {
                historyPopOver.hide();
            }
        }));
    }

    /**
     * Method create and show message history window
     */
    private void getHistoryPopOver() {
        historyPopOver = new PopOver();
        historyPopOver.setDetachable(false);
        historyPopOver.setAutoHide(false);
        historyPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        historyPopOver.setOnHidden((event) -> {
            historyPopOver.hide();
            messageSendHistoryBtn.setSelected(false);
        });

        ListView<String> list = new ListView<>();
        list.setMaxHeight(150);
        list.setMaxWidth(300);
        list.getStyleClass().add("history_list");
        list.setItems(sendMessageList);
        HBox historyPane = new HBox();
        historyPane.getChildren().addAll(list);
        historyPane.setPadding(new Insets(5, 0, 5, 0));

        // Set message text from history
        list.setCellFactory(col -> {
            final ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    String cellText = cell.getText();
                    if (cellText != null && !cellText.isEmpty() && !messageText.isDisable()) {
                        messageText.setText(cell.getText());
                        historyPopOver.hide();
                    }
                }
            });
            cell.setOnContextMenuRequested((event -> cell.setContextMenu(getHistoryContextMenu())));
            return cell;
        });
        historyPopOver.setContentNode(historyPane);
    }

    /**
     * Context menu for history pop over
     * @return ContextMenu
     */
    private ContextMenu getHistoryContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteAll = new MenuItem("Delete all");
        deleteAll.setOnAction((event -> {
            sendMessageList.clear();
            historyPopOver.hide();
            messageSendHistoryBtn.setDisable(true);
        }));
        contextMenu.getItems().add(deleteAll);
        return contextMenu;
    }

    /**
     * Connected to websocket server if connectionStatus = false
     * or disconnect from websocket server if connectionStatus = true
     */
    private void actionConnectDisconnect() {
        if (connectionStatus) {
            try {
                client.closeConnection();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                Dialogs.getExceptionDialog(e);
            }
        } else {
            if (!serverUrl.getText().isEmpty()) {
                Task task = new Task() {

                    @Override
                    protected Object call() throws Exception {
                        Platform.runLater(() -> {
                            serverUrl.setEditable(false);
                            connectBtn.setDisable(true);
                            connectBtn.setText("Connecting ...");
                        });
                        startClient();
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    }

    /**
     * Start websocket client
     */
    private void startClient() {
        try {
            client = new Client(new URI(serverUrl.getText()));
            client.setMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    if (filterList!= null && filterList.getItems().size() != 0) {
                        for (MenuItem item : filterList.getItems()) {
                            if (message.contains(item.getText())) {
                                setResponseMessage(message);
                                break;
                            }
                        }
                    } else {
                        setResponseMessage(message);
                    }
                }
            });
            connectionStatus = true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
            Platform.runLater(() -> Dialogs.getExceptionDialog(e));
        } finally {
            checkConnectionStatus();
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
                    do {
                        if (client != null && client.isOpenConnection()) {
                            setConnectStatus(true);
                        } else {
                            setConnectStatus(false);
                        }
                        Thread.sleep(CHECK_CONNECTION_STATUS_TIMEOUT);
                    } while (connectionStatus);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Set status disable or enable send message text field and button
     * @param isConnected boolean
     */
    private void setConnectStatus(boolean isConnected) {
        if (isConnected) {
            Platform.runLater(() -> {
                status.getStyleClass().clear();
                status.getStyleClass().add("connected");
                connectBtn.setText("Disconnect");
                connectBtn.setDisable(false);
                setCircleTooltip("Connected");
                messageText.setDisable(false);
                messageSendBtn.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                status.getStyleClass().clear();
                status.getStyleClass().add("disconnected");
                serverUrl.setEditable(true);
                connectBtn.setText("Connect");
                connectBtn.setDisable(false);
                setCircleTooltip("Disconnected");
                messageText.setDisable(true);
                messageSendBtn.setDisable(true);
                connectionStatus = false;
            });
        }
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
     * Add websocket response message to output text area
     * @param message String message
     */
    private void setResponseMessage(String message) {
        Platform.runLater(() -> {
            if (outputText != null) {
                outputText.appendText(String.format("%s\n", message));
            }
        });
    }

    /**
     * Send websocket message
     */
    private void sendWebsocketMessage() {
        if (!messageText.getText().isEmpty()) {
            sendMessageList.add(messageText.getText());
            if (client != null) {
                try {
                    client.sendMessage(messageText.getText());
                } catch (IOException e) {
                    Dialogs.getExceptionDialog(e);
                }
            }
            messageText.clear();
            messageText.requestFocus();
        }
        if (sendMessageList.size() > 0) {
            messageSendHistoryBtn.setDisable(false);
        }
    }

    /**
     * Set circle status tooltip message
     * @param message String
     */
    private void setCircleTooltip(String message) {
        if (statusTooltip == null) {
            statusTooltip = new Tooltip(message);
            Tooltip.install(status, statusTooltip);
        } else {
            statusTooltip.setText(message);
        }
    }
}
