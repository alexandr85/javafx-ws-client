package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.elements.headers.Header;
import ru.testing.client.elements.message.*;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.Utils;
import ru.testing.client.elements.popovers.HttpSettingPopOver;
import ru.testing.client.elements.popovers.SessionsPopOver;
import ru.testing.client.elements.sessions.ItemElement;
import ru.testing.client.elements.sessions.Session;
import ru.testing.client.websocket.Client;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 1000;
    private final ObservableList<String> sendMsgList = FXCollections.observableArrayList();
    private final ObservableList<OutputMessage> outputMessageList = FXCollections.observableArrayList();
    private final ObservableList<String> filterList = FXCollections.observableArrayList();
    private final org.controlsfx.tools.Platform platform = org.controlsfx.tools.Platform.getCurrent();
    private Client client;
    private List<Header> headers;
    private boolean connectionStatus;
    private Stage mainStage;
    private Tooltip statusTooltip;
    private HttpSettingPopOver httpSettingPopOver;
    private SessionsPopOver sessionsPopOver;
    private PopOver historyPopOver;
    private PopOver filterPopOver;

    /**
     * Menu buttons
     */
    @FXML
    private MenuItem saveOutputMenu;
    @FXML
    private MenuItem sessionsMenu;
    @FXML
    private MenuItem exitAppMenu;
    @FXML
    private CheckMenuItem showStatusBar;
    @FXML
    private CheckMenuItem autoScrollMenuItem;
    @FXML
    private CheckMenuItem showFilter;
    @FXML
    private CheckMenuItem showFind;

    /**
     * Text fields
     */
    @FXML
    private TextField serverUrl;
    @FXML
    protected TextField sendMsgTextField;
    @FXML
    private TextField filterTextField;
    @FXML
    private TextField findTextField;

    /**
     * Main buttons
     */
    @FXML
    private ToggleButton httpSettings;
    @FXML
    private Button connectBtn;
    @FXML
    private Button messageSendBtn;
    @FXML
    protected ToggleButton sendMsgHistoryBtn;
    @FXML
    private Button filterAddBtn;
    @FXML
    private ToggleButton filterListBtn;
    @FXML
    private ToggleButton filterOnOffBtn;
    @FXML
    private Button findTextBtn;

    /**
     * List views
     */
    @FXML
    private ListView<OutputMessage> outputTextView;

    /**
     * Labels
     */
    @FXML
    private Label filterStatusLabel;
    @FXML
    private Label timeDiffLabel;
    @FXML
    private Label autoScrollLabel;
    @FXML
    private Label filterCount;
    @FXML
    private Label outputMsgCount;
    @FXML
    private Label headersCount;

    /**
     * Other elements
     */
    @FXML
    private StatusBar statusBar;
    @FXML
    private FlowPane filterBar;
    @FXML
    private FlowPane findBar;
    @FXML
    private Circle connectStatus;

    /**
     * Main controller default contractor
     * @param mainStage Stage
     */
    public MainController(Stage mainStage) {
        this.mainStage = mainStage;
    }

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {

        // Default focus request
        Platform.runLater(() -> outputTextView.requestFocus());

        // Set hot keys
        setHotKey(exitAppMenu, KeyCode.X);
        setHotKey(saveOutputMenu, KeyCode.S);
        setHotKey(sessionsMenu, KeyCode.D);
        setHotKey(showStatusBar, KeyCode.B);
        setHotKey(autoScrollMenuItem, KeyCode.L);
        setHotKey(showFilter, KeyCode.J);
        setHotKey(showFind, KeyCode.F);

        // Set circle tooltip status
        setCircleTooltip("Disconnected");

        // Close application
        mainStage.setOnCloseRequest((event -> exitApplication()));

        // Update output message list view
        outputTextView.setItems(outputMessageList);
        outputTextView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outputTextView.setCellFactory(listView -> new OutputMessageCellFactory(outputMessageList));
        outputTextView.getItems().addListener(this::outputMessageListener);
        outputTextView.focusedProperty().addListener(observable -> {
            if (!outputTextView.isFocused()) {
                outputTextView.getSelectionModel().clearSelection();
            }
        });
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
                connectDisconnectAction();
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
                historyPopOver.show(sendMsgHistoryBtn, -7);
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
                int size = filterList.size();
                if (size > 0 && filterOnOffBtn.isSelected()) {
                    filterListBtn.setDisable(false);
                    filterCount.setText(String.valueOf(size));
                } else {
                    filterListBtn.setDisable(true);
                    filterListBtn.setSelected(false);
                    if (filterPopOver != null) {
                        filterPopOver.hide();
                    }
                    filterCount.setText("");
                }
            }
        });
        filterTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addToFilterList();
            }
        });
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
    private void connectDisconnectAction() {
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
        if (filterOnOffBtn.isSelected()) {
            filterOnOffBtn.setGraphic(new ImageView("/images/filter-on.png"));
            filterStatusLabel.setGraphic(new ImageView("/images/turn-on.png"));
            filterAddBtn.setDisable(false);
            filterTextField.setDisable(false);
            filterTextField.requestFocus();
            if (filterList.size() > 0) {
                filterListBtn.setDisable(false);
            }
        } else {
            filterOnOffBtn.setGraphic(new ImageView("/images/filter-off.png"));
            filterStatusLabel.setGraphic(new ImageView("/images/turn-off.png"));
            filterAddBtn.setDisable(true);
            filterTextField.setDisable(true);
            filterListBtn.setDisable(true);
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
        if (autoScrollMenuItem.isSelected()) {
            autoScrollLabel.setGraphic(new ImageView("/images/turn-on.png"));
        } else {
            autoScrollLabel.setGraphic(new ImageView("/images/turn-off.png"));
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
     * Show or hide find bar
     */
    @FXML
    private void changeFindVisible() {
        boolean status = showFind.isSelected();
        findBar.setVisible(status);
        findBar.setManaged(status);
    }

    /**
     * Show sessions pop over
     */
    @FXML
    private void showSessions() {
        if (sessionsPopOver == null) {
            sessionsPopOver = new SessionsPopOver(this);
        }
        if (!sessionsPopOver.isShowing()) {
            getMainParent().setDisable(true);
            sessionsPopOver.show(mainStage,
                    mainStage.getX() + mainStage.getWidth()/2 - sessionsPopOver.getPopOverWidth()/2,
                    mainStage.getY() + mainStage.getHeight()/2 - sessionsPopOver.getPopOverHeight()/2);
        }
    }

    /**
     * Get sessions pop over
     * @return SessionsPopOver
     */
    public SessionsPopOver getSessionsPopOver() {
        if (sessionsPopOver == null) {
            showSessions();
        }
        return sessionsPopOver;
    }

    /**
     * Get http settings pop over
     */
    @FXML
    private void showHttpSettings() {
        if (httpSettings.isSelected()) {
            if (httpSettingPopOver == null) {
                httpSettingPopOver = new HttpSettingPopOver(httpSettings, serverUrl, this);
            }
            httpSettingPopOver.show(httpSettings, -4);
        } else {
            httpSettingPopOver.hide();
        }
    }

    /**
     * Get websocket server url from field
     * @return String
     */
    public String getServerUrl() {
        return serverUrl.getText();
    }

    /**
     * Set data from selected session
     * @param session Session
     */
    public void setDataFromSession(Session session) {
        if (session != null) {
            serverUrl.setText(session.getServer().getUrl());

            // Set filter data
            filterOnOffBtn.setSelected(session.getFilterData().isFilterOn());
            filterList.clear();
            List<ItemElement> filterItems = session.getFilterData().getItems();
            if (filterItems != null) {
                filterItems.stream().forEach(item -> filterList.add(item.getValue()));
            }
            changeFilterStatus();

            // Set send message data
            sendMsgList.clear();
            List<ItemElement> sendMsgItems = session.getSendHistoryData().getItem();
            if (sendMsgItems != null) {
                sendMsgItems.stream().forEach(item -> sendMsgList.add(item.getValue()));
            }
        }
    }

    /**
     * Get send message list
     * @return List<ItemElement>
     */
    public List<ItemElement> getSendMsgList() {
        List<ItemElement> items = new ArrayList<>();
        sendMsgList.stream().forEach(s -> items.add(new ItemElement(s)));
        return items;
    }

    /**
     * Get on off filter status
     * @return boolean
     */
    public boolean getFilterStatus() {
        return filterOnOffBtn.isSelected();
    }

    /**
     * Get filter items list
     * @return List<ItemElement>
     */
    public List<ItemElement> getFilterItems() {
        List<ItemElement> items = new ArrayList<>();
        filterList.stream().forEach(s -> items.add(new ItemElement(s)));
        return items;
    }

    /**
     * Get message history pop over
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
            client = new Client();
            client.setEndpointURI(new URI(serverUrl.getText()));
            client.setHeaders(headers);
            client.openConnection();
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
                connectStatus.getStyleClass().clear();
                connectStatus.getStyleClass().add("connected");
                connectBtn.setText("Disconnect");
                connectBtn.setDisable(false);
                setCircleTooltip("Connected");
                sendMsgTextField.setDisable(false);
                messageSendBtn.setDisable(false);
                httpSettings.setDisable(true);
            });
        } else {
            Platform.runLater(() -> {
                connectStatus.getStyleClass().clear();
                connectStatus.getStyleClass().add("disconnected");
                serverUrl.setEditable(true);
                connectBtn.setText("Connect");
                connectBtn.setDisable(false);
                setCircleTooltip("Disconnected");
                sendMsgTextField.setDisable(true);
                messageSendBtn.setDisable(true);
                connectionStatus = false;
                httpSettings.setDisable(false);
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
    private void outputMessageListener(ListChangeListener.Change<? extends OutputMessage> change) {
        if (change.next()) {
            showAllMsgAndSelectedMsgCount();
            final int size = outputMessageList.size();
            if (size > 0 && autoScrollMenuItem.isSelected()) {
                outputTextView.scrollTo(size - 1);
            }
        }
    }

    /**
     * Show all output message and selected message count
     */
    private void showAllMsgAndSelectedMsgCount() {
        outputMsgCount.setText(String.format("%s/%s",
                outputMessageList.size(),
                outputTextView.getSelectionModel().getSelectedItems().size()));
    }

    /**
     * Show time diff between first and last selected message
     *
     * @param change ListChangeListener.Change<? extends OutputMessage>
     */
    private void selectedActions(ListChangeListener.Change<? extends OutputMessage> change) {
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

    /**
     * Set headers list
     * @param headers List<Header>
     */
    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    /**
     * Set custom header count
     * @param i int
     */
    public void setHeadersCount(int i) {
        headersCount.setText(String.valueOf(i));
    }

    /**
     * Set hot key for menu element
     * @param item MenuItem
     * @param keyCode KeyCode
     */
    private void setHotKey(MenuItem item, KeyCode keyCode) {
        KeyCombination.Modifier key = KeyCombination.CONTROL_DOWN;
        if (platform == org.controlsfx.tools.Platform.OSX) {
            key = KeyCombination.META_DOWN;
        }
        item.setAccelerator(new KeyCodeCombination(keyCode, key));
    }

    /**
     * Get main parent node
     * @return Parent
     */
    public Parent getMainParent() {
        return mainStage.getScene().getRoot();
    }
}