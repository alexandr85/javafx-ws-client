package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import org.apache.log4j.Logger;
import ru.testing.client.FXApp;
import ru.testing.client.common.Utils;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.properties.Settings;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.filter.FilterListPopOver;
import ru.testing.client.elements.tabs.WsMessageTab;
import ru.testing.client.elements.tabs.WsMessagesTab;
import ru.testing.client.websocket.*;

import java.net.URI;

/**
 * Controller for detail message tab form
 */
public class TabWsMessagesController {

    private static final Logger LOGGER = Logger.getLogger(TabWsMessagesController.class);
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 1000;
    private AppProperties props = AppProperties.getInstance();
    private final ObservableList<ReceivedMessage> receivedMessageList = FXCollections.observableArrayList();
    private final ObservableList<ReceivedMessage> receivedFilteredMessageList = FXCollections.observableArrayList();
    private final ObservableList<String> filterList = FXCollections.observableArrayList();
    private MainController mainController = FXApp.getMainController();
    private Settings settings;
    private FilterListPopOver filterPopOver;
    private SendMessagesPopOver sendMessagesPopOver;
    private Tooltip statusTooltip;
    private WsClient wsClient;
    private boolean filtered, autoScroll;

    @FXML
    private FlowPane sendMessagePane;
    @FXML
    private ListView<ReceivedMessage> outputTextView;
    @FXML
    private Label filterCount;
    @FXML
    private FlowPane filterBar;
    @FXML
    private ToggleButton sendMsgHistoryBtn;
    @FXML
    private Button filterAddBtn;
    @FXML
    private ToggleButton filterListBtn;
    @FXML
    private TextField sendMsgTextField;
    @FXML
    private TextField filterTextField;
    @FXML
    private Label filterStatusLabel;
    @FXML
    private Label timeDiffLabel;
    @FXML
    private Label autoScrollLabel;
    @FXML
    private Label outputMsgCount;
    @FXML
    private Label lbHeadersCounter;
    @FXML
    private Circle connectStatus;

    @FXML
    protected void initialize() {

        // Get settings
        settings = props.getSettings();
        autoScroll = settings.isAutoScroll();

        // Set message font size
        outputTextView.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));

        // Default focus request
        Platform.runLater(() -> outputTextView.requestFocus());

        // Update output message list view
        outputTextView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outputSetList(false);
        outputTextView.getItems().addListener(this::receivedMessageListener);
        outputTextView.getSelectionModel().getSelectedItems().addListener(this::selectedMessages);
        outputTextView.setStyle(String.format("-fx-font-size: %spx;", settings.getFontSize()));
        outputTextView.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                outputTextView.getSelectionModel().getSelectedItems().forEach(WsMessageTab::new);
            }
        });

        // Send message
        sendMsgTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendWebsocketMessage();
            }
        });

        // Filters
        filterList.addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                int size = filterList.size();
                if (size > 0) {
                    filterListBtn.setDisable(false);
                    filterCount.setText(String.valueOf(size));
                    receivedFilteredMessageList.clear();
                    receivedMessageList.forEach(message ->
                            filterList.forEach(filter -> {
                                if (message.getMessage().contains(filter)) {
                                    receivedFilteredMessageList.add(message);
                                }
                            }));
                    outputSetList(filtered);
                } else {
                    filterListBtn.setDisable(true);
                    filterListBtn.setSelected(false);
                    getFilterPopOver().hide();
                    filterCount.setText("");
                    receivedFilteredMessageList.clear();
                    outputSetList(false);
                }
            }
        });
        filterTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addToFilterList();
            }
        });

        // Set circle tooltip status
        setCircleTooltip("Disconnected");

        // Start ws client
        startWsClient();
    }

    /**
     * Send websocket message
     */
    @FXML
    private void sendWebsocketMessage() {
        String text = sendMsgTextField.getText().trim();
        if (!text.isEmpty()) {
            ObservableList<String> sendList = getSendMessagesPopOver().getController().getList();
            if (!sendList.contains(text)) {
                sendList.add(text);
            }
            addMessageToOutput(ReceivedMessageType.SEND, text);
            if (wsClient != null) {
                wsClient.sendMessage(text);
            }
            sendMsgTextField.clear();
        }
        sendMsgTextField.requestFocus();
    }

    @FXML
    private void changeFilterStatus() {
        Platform.runLater(() -> {
            if (filtered) {
                filterBar.setVisible(false);
                filterBar.setManaged(false);
                filterStatusLabel.setGraphic(new ImageView("/images/turn-off.png"));
                filterAddBtn.setDisable(true);
                filterTextField.setDisable(true);
                filterListBtn.setDisable(true);
                filtered = false;
                outputSetList(false);
                lbHeadersCounter.requestFocus();
            } else {
                filterBar.setVisible(true);
                filterBar.setManaged(true);
                filterStatusLabel.setGraphic(new ImageView("/images/turn-on.png"));
                filterAddBtn.setDisable(false);
                filterTextField.setDisable(false);
                filterTextField.requestFocus();
                if (filterList.size() > 0) {
                    filterListBtn.setDisable(false);
                    outputSetList(true);
                }
                filtered = true;
            }
        });
    }

    /**
     * Set on/off auto scroll output list status from menu bar
     */
    @FXML
    private void changeAutoScrollStatus() {
        Platform.runLater(() -> {
            if (autoScroll) {
                autoScrollLabel.setGraphic(new ImageView("/images/turn-off.png"));
                autoScroll = false;
            } else {
                autoScrollLabel.setGraphic(new ImageView("/images/turn-on.png"));
                autoScroll = true;
            }
        });
    }

    /**
     * Apply text filter for new response
     */
    @FXML
    private void addToFilterList() {
        String text = filterTextField.getText().trim();
        if (!text.isEmpty()) {
            filterList.add(text);
            filterTextField.clear();
        }
        filterTextField.requestFocus();
    }

    /**
     * Show message history pop over
     */
    @FXML
    private void showSendHistoryPopOver() {
        if (sendMsgHistoryBtn.isSelected()) {
            getSendMessagesPopOver().show(sendMsgHistoryBtn, -7);
        } else {
            getSendMessagesPopOver().hide();
        }
    }

    /**
     * Method create and show message history window
     */
    @FXML
    private void showFilterListPopOver() {
        if (filterListBtn.isSelected()) {
            getFilterPopOver().show(filterListBtn, -10);
        } else {
            getFilterPopOver().hide();
        }
    }

    /**
     * Get current websocket client
     *
     * @return WsClient
     */
    WsClient getWsClient() {
        return wsClient;
    }

    /**
     * Get received messages
     *
     * @return ObservableList<ReceivedMessage>
     */
    public ObservableList<ReceivedMessage> getReceivedMessageList() {
        return receivedMessageList;
    }

    /**
     * Start websocket wsClient
     */
    void startWsClient() {
        try {
            if (wsClient == null) {
                wsClient = new WsClient();
                wsClient.setEndpointURI(new URI(mainController.getServerUrl().getText()));
                wsClient.setHeaders(mainController.getHeadersList());
                wsClient.setSslValidate(settings.isWsSslValidate());
                wsClient.setWithCompression(settings.isWithCompression());
            }
            wsClient.openConnection();
            wsClient.setMessageHandler(new MessageHandler(this));
            getSendMessagesPopOver().getController().getSentMessages().forEach(sentMessage -> {
                if (wsClient != null && sentMessage.isAutoSend()) {
                    String msgValue = sentMessage.getValue();
                    wsClient.sendMessage(msgValue);
                    addMessageToOutput(ReceivedMessageType.SEND, msgValue);
                }
            });
        } catch (Exception e) {
            LOGGER.warn(String.format("Error open connection: %s", e.getLocalizedMessage()));
            Platform.runLater(() -> new Dialogs().getExceptionDialog(e));
        } finally {
            checkConnectionStatus();
        }
    }

    /**
     * Add websocket response message to output text area
     *
     * @param type    Message type
     * @param message String message
     */
    public void addMessageToOutput(ReceivedMessageType type, String message) {
        ReceivedMessage msg = new ReceivedMessage(type, message);
        if (filtered && filterList.size() > 0) {
            for (String filterItem : filterList) {
                if (message.contains(filterItem)) {
                    Platform.runLater(() -> {
                        receivedMessageList.add(new ReceivedMessage(type, message));
                        receivedFilteredMessageList.add(msg);
                    });
                    return;
                }
            }
            Platform.runLater(() -> receivedMessageList.add(msg));
        } else {
            Platform.runLater(() -> receivedMessageList.add(msg));
        }
    }

    /**
     * Scroll to last list message after new message added
     *
     * @param change ListChangeListener.Change<? extends ReceivedMessage>
     */
    private void receivedMessageListener(ListChangeListener.Change<? extends ReceivedMessage> change) {
        if (change.next()) {
            Platform.runLater(() -> {
                showAllMsgAndSelectedMsgCount();
                final int size = receivedMessageList.size();
                if (size > 0 && autoScroll) {
                    outputTextView.scrollTo(size - 1);
                }
            });
        }
    }

    /**
     * Show all output message and selected message count
     */
    private void showAllMsgAndSelectedMsgCount() {
        outputMsgCount.setText(String.format("%s/%s",
                receivedMessageList.size(),
                outputTextView.getSelectionModel().getSelectedItems().size()));
    }

    /**
     * Show time diff between first and last selected message
     *
     * @param change ListChangeListener.Change<? extends ReceivedMessage>
     */
    private void selectedMessages(ListChangeListener.Change<? extends ReceivedMessage> change) {
        if (change.next()) {
            showAllMsgAndSelectedMsgCount();
            int selectedSize = change.getList().size();
            if (selectedSize > 1 && change.wasAdded()) {
                long timeFirst = change.getList().get(0).getMilliseconds();
                long timeLast = change.getList().get(selectedSize - 1).getMilliseconds();
                timeDiffLabel.setText(Utils.getFormattedDiffTime(timeFirst, timeLast));
            } else {
                timeDiffLabel.setText("");
            }
        }
    }

    /**
     * Set output text list
     *
     * @param isFiltered boolean
     */
    private void outputSetList(boolean isFiltered) {
        if (isFiltered) {
            outputTextView.setItems(receivedFilteredMessageList);
        } else {
            outputTextView.setItems(receivedMessageList);
        }
        outputTextView.setCellFactory(listView -> new ReceivedMessageCellFactory(this, isFiltered));
    }

    /**
     * Get filter list pop over
     *
     * @return FilterListPopOver
     */
    private FilterListPopOver getFilterPopOver() {
        if (filterPopOver == null) {
            filterPopOver = new FilterListPopOver(this);
        }
        return filterPopOver;
    }

    /**
     * Get send message history pop over
     *
     * @return SendMessagesPopOver
     */
    public SendMessagesPopOver getSendMessagesPopOver() {
        if (sendMessagesPopOver == null) {
            sendMessagesPopOver = new SendMessagesPopOver(this);
        }
        return sendMessagesPopOver;
    }

    /**
     * Check connection status
     */
    void checkConnectionStatus() {
        Task task = new Task() {

            @Override
            protected Object call() {
                try {
                    do {
                        if (wsClient != null && wsClient.isOpenConnection()) {
                            setConnectStat(true);
                        } else {
                            setConnectStat(false);
                            break;
                        }
                        Thread.sleep(CHECK_CONNECTION_STATUS_TIMEOUT);
                    } while (wsClient != null);
                } catch (InterruptedException e) {
                    LOGGER.error(String.format("Thread interrupted exception: %s", e.getMessage()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public ListView<ReceivedMessage> getOutputTextView() {
        return outputTextView;
    }

    public ObservableList<ReceivedMessage> getReceivedFilteredMessageList() {
        return receivedFilteredMessageList;
    }


    public ObservableList<String> getFilterList() {
        return filterList;
    }

    public ToggleButton getFilterListBtn() {
        return filterListBtn;
    }

    public TextField getSendMsgTextField() {
        return sendMsgTextField;
    }

    public ToggleButton getSendMsgHistoryBtn() {
        return sendMsgHistoryBtn;
    }

    /**
     * Set status disable or enable send message text field and button
     *
     * @param isConnected boolean
     */
    private void setConnectStat(boolean isConnected) {
        Platform.runLater(() -> {
            Tab currentTab = mainController.getTabPane().getSelectionModel().getSelectedItem();
            Button connectionButton = null;
            if (currentTab instanceof WsMessagesTab) {
                if (((WsMessagesTab) currentTab).getController() == this) {
                    connectionButton = mainController.getConnectionButton();
                }
            }
            if (isConnected) {
                connectStatus.getStyleClass().clear();
                connectStatus.getStyleClass().add("connected");
                setCircleTooltip("Connected");
                sendMessagePane.setVisible(true);
                sendMessagePane.setManaged(true);
                if (connectionButton != null) {
                    connectionButton.setText("Disconnect");
                }
            } else {
                connectStatus.getStyleClass().clear();
                connectStatus.getStyleClass().add("disconnected");
                setCircleTooltip("Disconnected");
                sendMessagePane.setVisible(false);
                sendMessagePane.setManaged(false);
                if (connectionButton != null) {
                    connectionButton.setText("Connect");
                }
            }
        });
    }

    /**
     * Set circle status tooltip message
     *
     * @param message String
     */
    private void setCircleTooltip(String message) {
        if (statusTooltip == null) {
            statusTooltip = new Tooltip(message);
            Tooltip.install(connectStatus, statusTooltip);
        } else {
            statusTooltip.setText(message);
        }
    }
}
