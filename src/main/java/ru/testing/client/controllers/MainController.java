package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.AppProperties;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.Utils;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.filter.FilterListPopOver;
import ru.testing.client.elements.headers.Header;
import ru.testing.client.elements.headers.HeadersPopOver;
import ru.testing.client.elements.history.SendHistoryPopOver;
import ru.testing.client.elements.message.OutputMessage;
import ru.testing.client.elements.message.OutputMessageCellFactory;
import ru.testing.client.elements.message.OutputMessageFormat;
import ru.testing.client.elements.message.OutputMessageType;
import ru.testing.client.elements.sessions.SessionsPopOver;
import ru.testing.client.elements.sessions.session.ItemElement;
import ru.testing.client.elements.sessions.session.Session;
import ru.testing.client.websocket.Client;
import ru.testing.client.websocket.MessageHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.controlsfx.tools.Platform.OSX;

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
    private AppProperties properties;
    private Client client;
    private boolean connectionStatus, autoScrollStatus = true, filterStatus;
    private Stage mainStage;
    private Tooltip statusTooltip;
    private HeadersPopOver headersPopOver;
    private SessionsPopOver sessionsPopOver;
    private SendHistoryPopOver historyPopOver;
    private FilterListPopOver filterPopOver;

    /**
     * Menu buttons
     */
    @FXML
    private MenuBar menuBar;
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
     *
     * @param mainStage Stage
     */
    public MainController(Stage mainStage, AppProperties properties) {
        this.properties = properties;
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
        sendMsgList.addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                if (sendMsgList.size() > 0) {
                    sendMsgHistoryBtn.setDisable(false);
                } else {
                    sendMsgHistoryBtn.setDisable(true);
                    sendMsgHistoryBtn.setSelected(false);
                    getHistoryPopOver().hide();
                }
            }
        });

        // Filter
        filterList.addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                int size = filterList.size();
                if (size > 0 && filterOnOffBtn.isSelected()) {
                    filterListBtn.setDisable(false);
                    filterCount.setText(String.valueOf(size));
                } else {
                    filterListBtn.setDisable(true);
                    filterListBtn.setSelected(false);
                    getFilterPopOver().hide();
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
        if (filterStatus) {
            filterOnOffBtn.setSelected(false);
            filterOnOffBtn.setGraphic(new ImageView("/images/filter-off.png"));
            filterStatusLabel.setGraphic(new ImageView("/images/turn-off.png"));
            filterAddBtn.setDisable(true);
            filterTextField.setDisable(true);
            filterListBtn.setDisable(true);
            filterStatus = false;
        } else {
            filterOnOffBtn.setSelected(true);
            filterOnOffBtn.setGraphic(new ImageView("/images/filter-on.png"));
            filterStatusLabel.setGraphic(new ImageView("/images/turn-on.png"));
            filterAddBtn.setDisable(false);
            filterTextField.setDisable(false);
            filterTextField.requestFocus();
            if (filterList.size() > 0) {
                filterListBtn.setDisable(false);
            }
            filterStatus = true;
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
        } else {
            filterTextField.requestFocus();
        }
    }

    /**
     * Set on/off auto scroll output list status from menu bar
     */
    @FXML
    private void changeAutoScrollStatus() {
        if (autoScrollStatus) {
            autoScrollLabel.setGraphic(new ImageView("/images/turn-off.png"));
            autoScrollMenuItem.setSelected(false);
            autoScrollStatus = false;
        } else {
            autoScrollLabel.setGraphic(new ImageView("/images/turn-on.png"));
            autoScrollMenuItem.setSelected(true);
            autoScrollStatus = true;
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
     * Show sessions pop over
     */
    @FXML
    private void showSessions() {
        if (!getSessionsPopOver().isShowing()) {
            getMainParent().setDisable(true);
            getSessionsPopOver().show(
                    mainStage,
                    mainStage.getX() + mainStage.getWidth() / 2 - getSessionsPopOver().getPopOverWidth() / 2,
                    mainStage.getY() + mainStage.getHeight() / 2 - getSessionsPopOver().getPopOverHeight() / 2
            );
        }
    }

    /**
     * Get headers pop over
     */
    @FXML
    private void showHeadersPopOver() {
        if (httpSettings.isSelected()) {
            getHeadersPopOver().show(httpSettings, -4);
        } else {
            getHeadersPopOver().hide();
        }
    }

    /**
     * Show message history pop over
     */
    @FXML
    private void showSendHistoryPopOver() {
        if (sendMsgHistoryBtn.isSelected()) {
            getHistoryPopOver().show(sendMsgHistoryBtn, -7);
        } else {
            getHistoryPopOver().hide();
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
     * Go to web page for get last tag version
     */
    @FXML
    private void getLastTagVersionFromWeb() {
        goToWebPage(properties.getLastTagUrl());
    }

    /**
     * Go to web page for get about info
     */
    @FXML
    private void getAboutFromWeb() {
        goToWebPage(properties.getAboutUrl());
    }

    /**
     * Get http headers pop over
     *
     * @return HeadersPopOver
     */
    public HeadersPopOver getHeadersPopOver() {
        if (headersPopOver == null) {
            headersPopOver = new HeadersPopOver(httpSettings, serverUrl, this);
        }
        return headersPopOver;
    }

    /**
     * Get send message history pop over
     *
     * @return SendHistoryPopOver
     */
    public SendHistoryPopOver getHistoryPopOver() {
        if (historyPopOver == null) {
            historyPopOver = new SendHistoryPopOver(this);
        }
        return historyPopOver;
    }

    /**
     * Get sessions pop over
     *
     * @return SessionsPopOver
     */
    public SessionsPopOver getSessionsPopOver() {
        if (sessionsPopOver == null) {
            sessionsPopOver = new SessionsPopOver(this);
        }
        return sessionsPopOver;
    }

    /**
     * Get filter list pop over
     *
     * @return FilterListPopOver
     */
    public FilterListPopOver getFilterPopOver() {
        if (filterPopOver == null) {
            filterPopOver = new FilterListPopOver(this);
        }
        return filterPopOver;
    }

    /**
     * Send message history toggle button
     *
     * @return ToggleButton
     */
    public ToggleButton getSendMsgHistoryBtn() {
        return sendMsgHistoryBtn;
    }

    /**
     * Send message observable list
     *
     * @return ObservableList<String>
     */
    public ObservableList<String> getSendMsgList() {
        return sendMsgList;
    }

    /**
     * Send message field
     *
     * @return TextField
     */
    public TextField getSendMsgTextField() {
        return sendMsgTextField;
    }

    /**
     * Get filter list toggle button
     *
     * @return ToggleButton
     */
    public ToggleButton getFilterListBtn() {
        return filterListBtn;
    }

    /**
     * Application exit menu item
     *
     * @return MenuItem
     */
    public MenuItem getExitAppMenu() {
        return exitAppMenu;
    }

    /**
     * Application menu toolbar
     *
     * @return MenuBar
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Get websocket server url from field
     *
     * @return String
     */
    public String getServerUrl() {
        return serverUrl.getText();
    }

    /**
     * Set data from selected session
     *
     * @param session Session
     */
    public void setDataFromSession(Session session) {
        if (session != null) {

            // Set connection data
            serverUrl.setText(session.getConnect().getUrl());
            List<Header> headers = getHeadersList();
            headers.clear();
            List<ItemElement> items = session.getConnect().getHeaders();
            if (items != null) {
                items.stream().forEach(item -> headers.add(new Header(item.getName(), item.getValue())));
                setHeadersCount(items.size());
            }

            // Set filter data
            filterStatus = session.getFilterData().isFilterOn();
            filterList.clear();
            items = session.getFilterData().getItems();
            if (items != null) {
                items.stream().forEach(item -> filterList.add(item.getValue()));
                if (items.size() > 0) {
                    filterCount.setText(String.valueOf(items.size()));
                }
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
     *
     * @return List<ItemElement>
     */
    public List<ItemElement> getSendMsgItems() {
        List<ItemElement> items = new ArrayList<>();
        sendMsgList.stream().forEach(s -> items.add(new ItemElement(s)));
        return items;
    }

    /**
     * Get on off filter status
     *
     * @return boolean
     */
    public boolean getFilterStatus() {
        return filterStatus;
    }

    /**
     * Get observable filter list
     *
     * @return ObservableList<String>
     */
    public ObservableList<String> getFilterList() {
        return filterList;
    }

    /**
     * Get filter items list
     *
     * @return List<ItemElement>
     */
    public List<ItemElement> getFilterItems() {
        List<ItemElement> items = new ArrayList<>();
        filterList.stream().forEach(s -> items.add(new ItemElement(s)));
        return items;
    }

    /**
     * Start websocket client
     */
    private void startClient() {
        try {
            client = new Client();
            client.setEndpointURI(new URI(serverUrl.getText()));
            client.setHeaders(getHeadersList());
            client.openConnection();
            client.setMessageHandler(new MessageHandler(this));
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
        Platform.runLater(() -> {
            if (isConnected) {
                connectStatus.getStyleClass().clear();
                connectStatus.getStyleClass().add("connected");
                connectBtn.setText("Disconnect");
                connectBtn.setDisable(false);
                setCircleTooltip("Connected");
                sendMsgTextField.setDisable(false);
                messageSendBtn.setDisable(false);
                httpSettings.setDisable(true);
                sessionsMenu.setDisable(true);
            } else {
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
                sessionsMenu.setDisable(false);
            }
        });
    }

    /**
     * Add websocket response message to output text area
     *
     * @param type    Message type
     * @param message String message
     */
    public void addMessageToOutput(OutputMessageType type, String message) {
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
            if (size > 0 && autoScrollStatus) {
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
     * Get headers list
     *
     * @return List<ItemElement>
     */
    public List<ItemElement> getHeadersItems() {
        List<ItemElement> items = new ArrayList<>();
        List<Header> headers = getHeadersList();
        headers.stream().forEach(header -> items.add(new ItemElement(header.getName(), header.getValue())));
        return items;
    }

    /**
     * Get headers list
     *
     * @return ObservableList<Header>
     */
    private List<Header> getHeadersList() {
        return getHeadersPopOver().getHeadersController().getHeaderObservableList();
    }

    /**
     * Set custom header count
     *
     * @param i int
     */
    public void setHeadersCount(int i) {
        headersCount.setText(String.valueOf(i));
    }

    /**
     * Set hot key for menu element
     *
     * @param item    MenuItem
     * @param keyCode KeyCode
     */
    private void setHotKey(MenuItem item, KeyCode keyCode) {
        KeyCombination.Modifier key = KeyCombination.CONTROL_DOWN;
        if (platform == OSX) {
            key = KeyCombination.META_DOWN;
        }
        item.setAccelerator(new KeyCodeCombination(keyCode, key));
    }

    /**
     * Get main parent node
     *
     * @return Parent
     */
    public Parent getMainParent() {
        return mainStage.getScene().getRoot();
    }

    /**
     * Go to desktop browser page action
     *
     * @param url String
     */
    private void goToWebPage(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL(url).toURI());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}