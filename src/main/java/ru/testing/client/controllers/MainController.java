package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.cell.ImageGridCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.elements.message.*;
import ru.testing.client.common.profile.*;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.Utils;
import ru.testing.client.websocket.Client;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 1000;
    private final ObservableList<String> sendMsgList = FXCollections.observableArrayList();
    private final ObservableList<OutputMessage> outputMessageList = FXCollections.observableArrayList();
    private final ObservableList<String> filterList = FXCollections.observableArrayList();
    private Client client;
    private String cookieForRequest = "";
    private boolean connectionStatus;
    private Stage mainStage;
    private Tooltip statusTooltip;
    private PopOver historyPopOver;
    private PopOver filterPopOver;

    public MainController(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private TextField serverUrl;
    @FXML
    private ImageGridCell editCookie;
    @FXML
    private Button connectBtn;
    @FXML
    private Circle status;
    @FXML
    protected TextField sendMsgTextField;
    @FXML
    private Button messageSendBtn;
    @FXML
    protected ToggleButton sendMsgHistoryBtn;
    @FXML
    private ListView<OutputMessage> outputTextView;
    @FXML
    private Label autoScrollLabel;
    @FXML
    private CheckMenuItem autoScrollMenuItem;
    @FXML
    private CheckMenuItem showFilter;
    @FXML
    private VBox filterBar;
    @FXML
    private ToggleButton filterOnOffBtn;
    @FXML
    private TextField filterTextField;
    @FXML
    private Button filterAddBtn;
    @FXML
    private ToggleButton filterListBtn;
    @FXML
    private Label filterStatusLabel;
    @FXML
    private Label timeDiffLabel;
    @FXML
    private CheckMenuItem showStatusBar;
    @FXML
    private StatusBar statusBar;

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {

        // Set circle tooltip status
        setCircleTooltip("Disconnected");

        // Close application
        mainStage.setOnCloseRequest((event -> exitApplication()));

        // Update output message list view
        outputTextView.setItems(outputMessageList);
        outputTextView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outputTextView.setCellFactory((listView) -> new OutputMessageCellFactory(outputMessageList));
        outputTextView.getItems().addListener(this::scrollToLastMessage);
        outputTextView.getSelectionModel().getSelectedItems().addListener(this::selectedActions);
        outputTextView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Node cell = event.getPickResult().getIntersectedNode();
                if (cell instanceof OutputMessageCellFactory) {
                    OutputMessageCellFactory cellFactory = (OutputMessageCellFactory) cell;
                    outputTextView.getSelectionModel().clearSelection();
                    outputTextView.getSelectionModel().select(cellFactory.getItem());
                }
            }
        });

        // Connect or disconnect with websocket server
        serverUrl.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                actionConnectDisconnect();
            }
        });

        // Send message
        sendMsgTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendWebsocketMessage();
            }
        });
        sendMsgHistoryBtn.setOnAction(event -> {
            if (sendMsgHistoryBtn.isSelected()) {
                if (historyPopOver == null) {
                    getHistoryPopOver();
                }
                historyPopOver.show(sendMsgHistoryBtn, -10);
            } else {
                historyPopOver.hide();
            }
        });
        sendMsgList.addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                if (sendMsgList.size() > 0) {
                    sendMsgHistoryBtn.setDisable(false);
                } else {
                    sendMsgHistoryBtn.setDisable(true);
                    sendMsgHistoryBtn.setSelected(false);
                    if (historyPopOver != null) {
                        historyPopOver.hide();
                    }
                }
            }
        });

        // Filter
        filterListBtn.setOnAction(event -> {
            if (filterListBtn.isSelected()) {
                if (filterPopOver == null) {
                    getFilterPopOver();
                }
                filterPopOver.show(filterListBtn, -10);
            } else {
                filterPopOver.hide();
            }
        });
        filterList.addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                if (filterList.size() > 0 && filterOnOffBtn.isSelected()) {
                    filterListBtn.setDisable(false);
                } else {
                    filterListBtn.setDisable(true);
                    filterListBtn.setSelected(false);
                    if (filterPopOver != null) {
                        filterPopOver.hide();
                    }
                }
            }
        });
        filterTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addToFilterList();
            }
        });
    }

    @FXML
    private void setCookieForRequest() {
        cookieForRequest = Dialogs.getTextInputDialog(cookieForRequest, "Set request cookie");
        LOGGER.debug("Cookie value: {}", cookieForRequest);
    }

    /**
     * Close application with connection
     */
    @FXML
    private void exitApplication() {
        if (client != null && client.isOpenConnection()) {
            client.closeConnection();
        }
        Platform.exit();
        System.exit(0);
    }

    /**
     * Connected to websocket server if connectionStatus = false
     * or disconnect from websocket server if connectionStatus = true
     */
    @FXML
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
     * Send websocket message
     */
    @FXML
    private void sendWebsocketMessage() {
        String sendMsg = sendMsgTextField.getText();
        if (!sendMsg.isEmpty()) {
            sendMsgList.add(sendMsg);
            addMessageToOutput(OutputMessageType.SEND, sendMsg);
            if (client != null) {
                try {
                    client.sendMessage(sendMsg);
                } catch (IOException e) {
                    Dialogs.getExceptionDialog(e);
                }
            }
            sendMsgTextField.clear();
            sendMsgTextField.requestFocus();
        }
        if (sendMsgList.size() > 0) {
            sendMsgHistoryBtn.setDisable(false);
        }
    }

    @FXML
    private void changeFilterStatus() {
        String text = "Filter ";
        if (filterOnOffBtn.isSelected()) {
            filterTextField.setDisable(false);
            filterAddBtn.setDisable(false);
            if (filterList.size() > 0) {
                filterListBtn.setDisable(false);
            }
            filterOnOffBtn.setText(text.concat("on"));
            filterStatusLabel.setText(text.concat("on"));
            filterTextField.requestFocus();
        } else {
            filterTextField.setDisable(true);
            filterAddBtn.setDisable(true);
            filterListBtn.setDisable(true);
            filterOnOffBtn.setText(text.concat("off"));
            filterStatusLabel.setText(text.concat("off"));
        }
    }

    /**
     * Apply text filter for new response
     */
    @FXML
    private void addToFilterList() {
        if (!filterTextField.getText().isEmpty()) {
            filterList.add(filterTextField.getText());
            filterTextField.clear();
            filterTextField.requestFocus();
        }
    }

    /**
     * Set on/off auto scroll output list status from menu bar
     */
    @FXML
    private void changeAutoScrollStatus() {
        String text = "Auto scroll ";
        if (autoScrollMenuItem.isSelected()) {
            autoScrollLabel.setText(text.concat("on"));
        } else {
            autoScrollLabel.setText(text.concat("off"));
        }
    }

    /**
     * Save output message to text file
     */
    @FXML
    private void saveOutputToFile() {
        StringBuilder builder = new StringBuilder();
        for (OutputMessage message : outputMessageList) {
            builder.append(String.format(OutputMessageFormat.DEFAULT.getFormat().concat("\n"),
                    message.getFormattedTime(), message.getMessage()));
        }
        new FilesOperations().saveTextToFile(builder.toString());
    }

    /**
     * Load profile data from xml file
     */
    @FXML
    private void loadProfileData() {
        Profile profile = new FilesOperations().loadProfileData();
        if (profile != null) {
            try {
                // Set ws server url
                serverUrl.setText(profile.getServer().getUrl());

                // Set send history
                if (profile.getSendHistoryData() != null) {
                    List<ItemElement> sendMessageList = profile.getSendHistoryData().getItem();
                    if (sendMessageList.size() > 0) {
                        sendMsgList.clear();
                        sendMsgList.addAll(sendMessageList.stream().map(ItemElement::getValue).collect(Collectors.toList()));
                        sendMsgHistoryBtn.setDisable(false);
                    }
                }

                // Set auto scroll status
                if (profile.getOutputData().isAutoScrollOn()) {
                    autoScrollMenuItem.setSelected(true);
                    changeAutoScrollStatus();
                }

                // Set filter
                if (profile.getFilterData() != null) {
                    if (profile.getFilterData().isFilterOn()) {
                        filterOnOffBtn.setSelected(true);
                        changeFilterStatus();
                    }
                    List<ItemElement> filterList = profile.getFilterData().getItem();
                    if (filterList != null && filterList.size() > 0) {
                        this.filterList.clear();
                        this.filterList.addAll(filterList.stream().map(ItemElement::getValue).collect(Collectors.toList()));
                    }
                }
                Dialogs.getInfoDialog("Profile load successful");
            } catch (Exception e) {
                LOGGER.error("Error loading profile.xml: {}", e.getMessage());
                Dialogs.getExceptionDialog(e);
            }
        }
    }

    /**
     * Save profile data to xml
     */
    @FXML
    private void saveProfileData() {
        Profile profile = new Profile();

        // Save ws server url
        profile.setServer(new ServerData(serverUrl.getText()));

        // Save send history
        if (sendMsgList.size() > 0) {
            List<ItemElement> sendMessageList = new ArrayList<>();
            sendMessageList.addAll(sendMsgList.stream().map(ItemElement::new).collect(Collectors.toList()));
            profile.setSendHistoryData(new SendHistoryData(sendMessageList));
        }

        // Save auto scroll status
        profile.setOutputData(new OutputData(autoScrollMenuItem.isSelected()));

        // Save filter
        FilterData filter = new FilterData(filterOnOffBtn.isSelected());
        if (filterList.size() > 0) {
            List<ItemElement> historyList = new ArrayList<>();
            historyList.addAll(filterList.stream().map(ItemElement::new).collect(Collectors.toList()));
            filter.setItem(historyList);
        }
        profile.setFilterData(filter);
        new FilesOperations().saveProfileData(profile);
    }

    /**
     * Show or hide status bar
     */
    @FXML
    private void changesStatusBarVisible() {
        boolean status = showStatusBar.isSelected();
        statusBar.setVisible(status);
        statusBar.setManaged(status);
    }

    /**
     * Show or hide filter bar
     */
    @FXML
    private void changeFilterVisible() {
        boolean status = showFilter.isSelected();
        filterBar.setVisible(status);
        filterBar.setManaged(status);
    }

    /**
     * Method create and show message history window
     */
    private void getHistoryPopOver() {
        historyPopOver = new PopOver();
        historyPopOver.setDetachable(false);
        historyPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        historyPopOver.setOnHidden((event) -> sendMsgHistoryBtn.setSelected(false));
        ListView<String> list = new ListView<>();
        list.setMaxHeight(150);
        list.setMaxWidth(300);
        list.getStyleClass().add("pop_over_list");
        list.setItems(sendMsgList);
        list.setCellFactory(listView -> new SendHistoryCellFactory(sendMsgList, sendMsgTextField, historyPopOver));
        historyPopOver.setContentNode(list);
    }

    /**
     * Method create and show message history window
     */
    private void getFilterPopOver() {
        filterPopOver = new PopOver();
        filterPopOver.setDetachable(false);
        filterPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        filterPopOver.setOnHidden((event -> filterListBtn.setSelected(false)));
        ListView<String> list = new ListView<>();
        list.setMaxHeight(150);
        list.setMaxWidth(300);
        list.getStyleClass().add("pop_over_list");
        list.setItems(filterList);
        list.setCellFactory(listView -> new FilterCellFactory(filterList));
        filterPopOver.setContentNode(list);
    }

    /**
     * Start websocket client
     */
    private void startClient() {
        try {
            client = new Client(new URI(serverUrl.getText()), cookieForRequest);
            client.setMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    if (filterOnOffBtn.isSelected() && filterList.size() > 0) {
                        for (String filterItem : filterList) {
                            if (message.contains(filterItem)) {
                                addMessageToOutput(OutputMessageType.RECEIVED, message);
                                break;
                            }
                        }
                    } else {
                        addMessageToOutput(OutputMessageType.RECEIVED, message);
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
     *
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
                sendMsgTextField.setDisable(false);
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
                sendMsgTextField.setDisable(true);
                messageSendBtn.setDisable(true);
                connectionStatus = false;
            });
        }
    }

    /**
     * Add websocket response message to output text area
     *
     * @param type    Message type
     * @param message String message
     */
    private void addMessageToOutput(OutputMessageType type, String message) {
        Platform.runLater(() -> outputMessageList.add(new OutputMessage(type, message)));
    }

    /**
     * Scroll to last list message after new message added
     *
     * @param change ListChangeListener.Change<? extends OutputMessage>
     */
    private void scrollToLastMessage(ListChangeListener.Change<? extends OutputMessage> change) {
        if (change.next()) {
            final int size = outputMessageList.size();
            if (size > 0 && autoScrollMenuItem.isSelected()) {
                outputTextView.scrollTo(size - 1);
            }
        }
    }

    /**
     * Show time diff between first and last selected message
     *
     * @param change ListChangeListener.Change<? extends OutputMessage>
     */
    private void selectedActions(ListChangeListener.Change<? extends OutputMessage> change) {
        if (change.next()) {
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
     * Set circle status tooltip message
     *
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
