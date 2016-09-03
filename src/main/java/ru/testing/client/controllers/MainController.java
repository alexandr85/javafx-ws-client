package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.Utils;
import ru.testing.client.common.db.objects.Header;
import ru.testing.client.common.db.objects.RxMessage;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.filter.FilterListPopOver;
import ru.testing.client.elements.headers.HeadersPopOver;
import ru.testing.client.elements.history.SendHistoryPopOver;
import ru.testing.client.elements.message.OutputMessage;
import ru.testing.client.elements.message.OutputMessageCellFactory;
import ru.testing.client.elements.message.OutputMessageFormat;
import ru.testing.client.elements.message.OutputMessageType;
import ru.testing.client.elements.settings.SettingsTab;
import ru.testing.client.websocket.Client;
import ru.testing.client.websocket.MessageHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.controlsfx.tools.Platform.OSX;
import static ru.testing.client.common.db.Data.getData;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private static final int CHECK_CONNECTION_STATUS_TIMEOUT = 1000;
    private final ObservableList<String> sendMsgList = FXCollections.observableArrayList();
    private final ObservableList<OutputMessage> outputMessageList = FXCollections.observableArrayList();
    private final ObservableList<OutputMessage> outputFilteredMessageList = FXCollections.observableArrayList();
    private final ObservableList<String> filterList = FXCollections.observableArrayList();
    private final org.controlsfx.tools.Platform platform = org.controlsfx.tools.Platform.getCurrent();
    private AppProperties properties;
    private Client client;
    private boolean connectionStatus, autoScroll, filtered;
    private Stage mainStage;
    private Tooltip statusTooltip;
    private HeadersPopOver headersPopOver;
    private SendHistoryPopOver historyPopOver;
    private FilterListPopOver filterPopOver;
    private SettingsTab settingsTab;

    /**
     * Tabs
     */
    @FXML
    private TabPane tabPane;

    /**
     * Menu buttons
     */
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem saveOutputMenu;
    @FXML
    private MenuItem settingsMenu;
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
    private TextField sendMsgTextField;
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
    private ToggleButton sendAfterConnect;
    @FXML
    private Button connectBtn;
    @FXML
    private Button messageSendBtn;
    @FXML
    private ToggleButton sendMsgHistoryBtn;
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
    @FXML
    private ProgressBar progress;

    /**
     * Main controller default contractor
     *
     * @param mainStage Stage
     */
    public MainController(Stage mainStage) {
        this.properties = AppProperties.getAppProperties();
        this.mainStage = mainStage;
    }

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {

        // Tabs change listener
        tabPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            if (c.next()) {
                final StackPane header = (StackPane) tabPane.lookup(".tab-header-area");
                if(header != null) {
                    if(tabPane.getTabs().size() == 1) {
                        header.setStyle("-fx-pref-height: 0");
                    } else {
                        header.setStyle("-fx-pref-height: 30");
                    }
                }
            }
        });

        // Default focus request
        Platform.runLater(() -> outputTextView.requestFocus());

        // Set hot keys
        setHotKey(exitAppMenu, KeyCode.X);
        setHotKey(saveOutputMenu, KeyCode.S);
        setHotKey(settingsMenu, KeyCode.COMMA);
        setHotKey(showStatusBar, KeyCode.B);
        setHotKey(autoScrollMenuItem, KeyCode.L);
        setHotKey(showFilter, KeyCode.F);

        // Set circle tooltip status
        setCircleTooltip("Disconnected");

        // Close application
        mainStage.setOnCloseRequest((event -> exitApplication()));

        // Update output message list view
        outputTextView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outputSetList(false);
        outputTextView.getItems().addListener(this::outputMessageListener);
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

        // Filters
        filterList.addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                int size = filterList.size();
                if (size > 0) {
                    filterListBtn.setDisable(false);
                    filterCount.setText(String.valueOf(size));
                    outputFilteredMessageList.clear();
                    outputMessageList.forEach(message ->
                            filterList.forEach(filter -> {
                                if (message.getMessage().contains(filter)) {
                                    outputFilteredMessageList.add(message);
                                }
                            }));
                    outputSetList(true);
                } else {
                    filterListBtn.setDisable(true);
                    filterListBtn.setSelected(false);
                    getFilterPopOver().hide();
                    filterCount.setText("");
                    outputFilteredMessageList.clear();
                    outputSetList(false);
                }
            }
        });
        filterTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addToFilterList();
            }
        });

        // Load default session
        setDataFromSession(1);
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
            client.closeConnection();
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
                    new Dialogs().getExceptionDialog(e);
                }
            }
            sendMsgTextField.clear();
        }
        sendMsgTextField.requestFocus();
    }

    @FXML
    private void changeFilterStatus() {
        Platform.runLater(() -> {
            if (isFiltered()) {
                filterOnOffBtn.setSelected(false);
                filterOnOffBtn.setGraphic(new ImageView("/images/filter-off.png"));
                filterStatusLabel.setGraphic(new ImageView("/images/turn-off.png"));
                filterAddBtn.setDisable(true);
                filterTextField.setDisable(true);
                filterListBtn.setDisable(true);
                filtered = false;
                outputSetList(false);
                headersCount.requestFocus();
            } else {
                filterOnOffBtn.setSelected(true);
                filterOnOffBtn.setGraphic(new ImageView("/images/filter-on.png"));
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
     * Apply text filter for new response
     */
    @FXML
    private void addToFilterList() {
        if (!filterTextField.getText().isEmpty()) {
            filterList.add(filterTextField.getText());
            filterTextField.clear();
        }
        filterTextField.requestFocus();
    }

    /**
     * Set on/off auto scroll output list status from menu bar
     */
    @FXML
    private void changeAutoScrollStatus() {
        Platform.runLater(() -> {
            if (autoScroll) {
                autoScrollLabel.setGraphic(new ImageView("/images/turn-off.png"));
                autoScrollMenuItem.setSelected(false);
                autoScroll = false;
            } else {
                autoScrollLabel.setGraphic(new ImageView("/images/turn-on.png"));
                autoScrollMenuItem.setSelected(true);
                autoScroll = true;
            }
        });
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
        new FilesOperations().saveTextToFile(builder.toString(), this);
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
     * Show settings tab
     */
    @FXML
    private void showSettings() {
        if (settingsTab == null) {
            settingsTab = new SettingsTab(this);
        }
        settingsTab.showTab();
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
        headersCount.requestFocus();
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
        goToWebPage(properties.getLastReleaseUrl());
    }

    /**
     * Go to web page for get about info
     */
    @FXML
    private void getAboutFromWeb() {
        goToWebPage(properties.getAboutUrl());
    }

    /**
     * Set output text list
     * @param isFiltered boolean
     */
    private void outputSetList(boolean isFiltered) {
        if (isFiltered) {
            outputTextView.setItems(outputFilteredMessageList);
            outputTextView.setCellFactory(listView -> new OutputMessageCellFactory(outputFilteredMessageList, this));
        } else {
            outputTextView.setItems(outputMessageList);
            outputTextView.setCellFactory(listView -> new OutputMessageCellFactory(outputMessageList, this));
        }
    }

    /**
     * Get http headers pop over
     *
     * @return HeadersPopOver
     */
    private HeadersPopOver getHeadersPopOver() {
        if (headersPopOver == null) {
            headersPopOver = new HeadersPopOver(this);
        }
        return headersPopOver;
    }

    /**
     * Get send message history pop over
     *
     * @return SendHistoryPopOver
     */
    private SendHistoryPopOver getHistoryPopOver() {
        if (historyPopOver == null) {
            historyPopOver = new SendHistoryPopOver(this);
        }
        return historyPopOver;
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
     * Get tab pane element
     *
     * @return TabPane
     */
    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * Get application properties
     *
     * @return AppProperties
     */
    public AppProperties getProperties() {
        return properties;
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
    String getServerUrl() {
        return serverUrl.getText();
    }

    /**
     * Set data from selected session
     *
     * @param sessionId int
     */
    public void setDataFromSession(int sessionId) {
        setProgressVisible(true);
        Profile s = getData().getProfile(sessionId);
        if (s != null) {
            LOGGER.debug("Load session name: {}", s.getName());

            // set websocket server url
            serverUrl.setText(s.getUrl());

            // set filter properties
            showFilter.setSelected(s.getFilterShow());
            changeFilterVisible();
            filtered = !s.getFilterOn();
            changeFilterStatus();
            filterList.clear();
            s.getFilters().forEach(filterList::add);
            filterCount.setText(String.valueOf(filterList.size()));

            // set auto scroll properties
            autoScroll = !s.getAutoScroll();
            changeAutoScrollStatus();

            // set status bar properties
            showStatusBar.setSelected(s.getBarShow());
            changesStatusBarVisible();

            // set headers data
            getHeadersPopOver().getHeadersController().setHeaders(s.getHeaders());

            // set send messages
            sendMsgList.clear();
            s.getTxMessages().forEach(m -> sendMsgList.add(m.getValue()));

            // set output messages
            outputMessageList.clear();
            s.getRxMessages().forEach(m ->
                    outputMessageList.add(new OutputMessage(m.getTime(), m.getValue())));

        }
        setProgressVisible(false);
    }

    /**
     * Get send message list
     *
     * @return List<ItemElement>
     */
    List<String> getSendMsgItems() {
        List<String> items = new ArrayList<>();
        sendMsgList.forEach(items::add);
        return items;
    }

    /**
     * Get received message list
     *
     * @return List<RxMessage>
     */
    List<RxMessage> getOutputMessageList() {
        List<RxMessage> rxMessages = new ArrayList<>();
        outputMessageList.forEach(m -> rxMessages.add(new RxMessage(m.getFormattedTime(), m.getMessage())));
        return rxMessages;
    }

    /**
     * Get output message list view
     *
     * @return ListView<OutputMessage>
     */
    public ListView<OutputMessage> getOutputTextView() {
        return outputTextView;
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
     * Get headers list
     *
     * @return ObservableList<Header>
     */
    List<Header> getHeadersList() {
        return getHeadersPopOver().getHeadersController().getHeaderObservableList();
    }

    /**
     * Add websocket response message to output text area
     *
     * @param type    Message type
     * @param message String message
     */
    public void addMessageToOutput(OutputMessageType type, String message) {
        if (isFiltered() && filterList.size() > 0) {
            for (String filterItem : filterList) {
                if (message.contains(filterItem)) {
                    Platform.runLater(() -> {
                        outputMessageList.add(new OutputMessage(type, message));
                        outputFilteredMessageList.add(new OutputMessage(type, message));
                    });
                    break;
                }
            }
        } else {
            Platform.runLater(() -> outputMessageList.add(new OutputMessage(type, message)));
        }
    }

    /**
     * Get headers count label
     *
     * @return Label
     */
    public Label getHeadersCount() {
        return headersCount;
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
     * Set visible progress bar
     *
     * @param isVisible boolean
     */
    public void setProgressVisible(boolean isVisible) {
        Platform.runLater(() -> progress.setVisible(isVisible));
    }

    /**
     * Get on off filter status
     *
     * @return boolean
     */
    boolean isFiltered() {
        return filtered;
    }

    /**
     * Get visible filter pane status
     *
     * @return boolean
     */
    boolean isFilterVisible() {
        return showFilter.isSelected();
    }

    /**
     * Get auto scroll status
     *
     * @return boolean
     */
    boolean isAutoScroll() {
        return autoScroll;
    }

    /**
     * Get status bar visible status
     *
     * @return boolean
     */
    boolean isStatusBarShow() {
        return statusBar.isVisible();
    }

    /**
     * Get http settings button
     *
     * @return ToggleButton
     */
    public ToggleButton getHttpSettings() {
        return httpSettings;
    }

    /**
     * Start websocket client
     */
    private void startClient() {
        try {
            setProgressVisible(true);
            client = new Client();
            client.setEndpointURI(new URI(serverUrl.getText()));
            client.setHeaders(getHeadersList());
            client.openConnection();
            client.setMessageHandler(new MessageHandler(this));
            connectionStatus = true;
        } catch (Exception e) {
            LOGGER.error("Error open connection: {}", e.getLocalizedMessage());
            Platform.runLater(() -> new Dialogs().getExceptionDialog(e));
        } finally {
            setProgressVisible(false);
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
                    LOGGER.error("Thread interrupted exception{}", e.getMessage());
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
            }
        });
    }

    /**
     * Scroll to last list message after new message added
     *
     * @param change ListChangeListener.Change<? extends OutputMessage>
     */
    private void outputMessageListener(ListChangeListener.Change<? extends OutputMessage> change) {
        if (change.next()) {
            Platform.runLater(() -> {
                showAllMsgAndSelectedMsgCount();
                final int size = outputMessageList.size();
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
     * Go to desktop browser page action
     *
     * @param url String
     */
    private void goToWebPage(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL(url).toURI());
            } catch (IOException | URISyntaxException e) {
                LOGGER.error("Error go to web page: {}", e.getMessage());
            }
        }
    }
}