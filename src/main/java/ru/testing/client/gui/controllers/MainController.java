package ru.testing.client.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.commons.OutputFormat;
import ru.testing.client.gui.tools.Dialogs;
import ru.testing.client.commons.MessageType;
import ru.testing.client.gui.message.OutputMessage;
import ru.testing.client.gui.message.OutputMessageCell;
import ru.testing.client.gui.tools.FilesOperations;
import ru.testing.client.websocket.Client;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 1000;
    private static Client client;
    private final ObservableList<String> sendMessageList = FXCollections.observableArrayList();
    private boolean connectionStatus;
    private Stage mainStage;
    private Tooltip statusTooltip;
    private PopOver historyPopOver;

    public MainController(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML private MenuItem exitPlatform;
    @FXML private TextField serverUrl;
    @FXML private Button connectBtn;
    @FXML private Circle status;
    @FXML protected TextField messageText;
    @FXML private Button messageSendBtn;
    @FXML protected ToggleButton messageSendHistoryBtn;
    @FXML private ListView<OutputMessage> outputText;
    @FXML private ToggleButton autoScroll;
    @FXML private ToggleButton filterOnOffBtn;
    @FXML private TextField filterText;
    @FXML private Button filterAddBtn;
    @FXML private MenuButton filterList;
    @FXML private Label timeDiffLabel;

    /**
     * Method run then this controller initialize
     */
    @FXML private void initialize() {

        exitPlatform.setOnAction((event1 -> Platform.exit()));

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

        // Update output message list view
        outputText.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outputText.setCellFactory((listView) -> new OutputMessageCell(outputText));
        outputText.getItems().addListener((ListChangeListener<OutputMessage>) c -> {
            c.next();
            final int size = outputText.getItems().size();
            if (size > 0 && autoScroll.isSelected()) {
                outputText.scrollTo(size - 1);
            }
        });
        outputText.getSelectionModel().getSelectedItems().addListener((ListChangeListener<OutputMessage>) c -> {
            if (c.next()) {
                int selectedSize = c.getList().size();
                if (selectedSize > 1) {
                    long timeFirst = c.getList().get(0).getMilliseconds();
                    long timeLast = c.getList().get(selectedSize - 1).getMilliseconds();
                    long timeDiff = timeLast - timeFirst;
                    long ms = timeDiff % 1000;
                    long sec = TimeUnit.MILLISECONDS.toSeconds(timeDiff) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff));
                    long min = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
                    if (min > 29) {
                        timeDiffLabel.setText("Time diff > 30m");
                    } else if (min > 0) {
                        timeDiffLabel.setText(String.format("%dm %ds %dms", min, sec, ms));
                    } else if (sec > 0) {
                        timeDiffLabel.setText(String.format("%ds %dms", sec, ms));
                    } else {
                        timeDiffLabel.setText(String.format("%dms", ms));
                    }
                } else {
                    timeDiffLabel.setText("");
                }
            }
        });
        outputText.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (event.getPickResult().getIntersectedNode() instanceof OutputMessageCell) {
                    OutputMessageCell cell = (OutputMessageCell) event.getPickResult().getIntersectedNode();
                    outputText.getSelectionModel().clearSelection();
                    outputText.getSelectionModel().select(cell.getItem());
                }
            }
        });

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

        // Active filtering
        filterOnOffBtn.setOnAction((action) -> {
            if (filterOnOffBtn.isSelected()) {
                filterText.setDisable(false);
                filterAddBtn.setDisable(false);
                if (filterList.getItems().size() > 0) {
                    filterList.setDisable(false);
                }
                filterOnOffBtn.setText("Filter on");
            } else {
                filterText.setDisable(true);
                filterAddBtn.setDisable(true);
                filterList.setDisable(true);
                filterOnOffBtn.setText("Filter off");
            }
        });

        // Add filter
        filterAddBtn.setOnMouseClicked((event -> addToFilterList()));
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
     * Save output message to text file
     */
    @FXML protected void saveOutputToFile() {
        StringBuilder builder = new StringBuilder();
        for (OutputMessage message : outputText.getItems()) {
            builder.append(String.format(OutputFormat.DEFAULT.getFormat().concat("\n"),
                    message.getFormattedTime(), message.getMessage()));
        }
        new FilesOperations().saveTextToFile(builder.toString());
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
        list.setCellFactory(listView -> {
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
        MenuItem deleteAll = new MenuItem("Clear all");
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
                            connectBtn.setText("Connecting");
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
                    if (filterOnOffBtn.isSelected() && filterList!= null && filterList.getItems().size() != 0) {
                        for (MenuItem item : filterList.getItems()) {
                            if (message.contains(item.getText())) {
                                addMessageToOutput(MessageType.RECEIVED, message);
                                break;
                            }
                        }
                    } else {
                        addMessageToOutput(MessageType.RECEIVED, message);
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
     * @param type Message type
     * @param message String message
     */
    private void addMessageToOutput(MessageType type, String message) {
        Platform.runLater(() -> {
            if (outputText != null) {
                outputText.getItems().add(new OutputMessage(type, message));
            }
        });
    }

    /**
     * Send websocket message
     */
    private void sendWebsocketMessage() {
        String sendMsg = messageText.getText();
        if (!sendMsg.isEmpty()) {
            sendMessageList.add(sendMsg);
            addMessageToOutput(MessageType.SEND, sendMsg);
            if (client != null) {
                try {
                    client.sendMessage(sendMsg);
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
