package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.db.objects.*;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.headers.HeadersPopOver;
import ru.testing.client.elements.tabs.SettingsTab;
import ru.testing.client.elements.tabs.RestTab;
import ru.testing.client.elements.tabs.WsMessagesTab;
import ru.testing.client.websocket.ReceivedMessageFormat;
import ru.testing.client.websocket.WsClient;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.controlsfx.tools.Platform.OSX;
import static ru.testing.client.MainApp.getPrimaryStage;

/**
 * FXML controller for main page
 */
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    private final ObservableList<HttpTypes> httpTypes = FXCollections.observableArrayList();
    private final org.controlsfx.tools.Platform platform = org.controlsfx.tools.Platform.getCurrent();
    private List<WsClient> wsClients = new ArrayList<>();
    private AppProperties properties = AppProperties.getAppProperties();
    private HeadersPopOver headersPopOver;
    private SettingsTab settingsTab;

    @FXML
    private MenuBar menuBar;
    @FXML
    private ComboBox<HttpTypes> httpTypesComboBox;
    @FXML
    private CustomTextField serverUrl;
    @FXML
    private Label urlCleaner;
    @FXML
    private ToggleButton httpSettings;
    @FXML
    private Button connectionButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private MenuItem saveOutputMenu;
    @FXML
    private MenuItem settingsMenu;
    @FXML
    private MenuItem exitAppMenu;
    @FXML
    private TilePane noResults;
    @FXML
    private ProgressBar progress;

    /**
     * Method run then this controller initialize
     */
    @FXML
    protected void initialize() {

        // Close application
        getPrimaryStage().setOnCloseRequest((event -> exitApplication()));

        // Tabs change listener
        tabPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            if (c.next()) {
                final StackPane header = (StackPane) tabPane.lookup(".tab-header-area");
                if (header != null) {
                    if (tabPane.getTabs().size() == 0) {
                        header.setStyle("-fx-pref-height: 0");
                        noResults.setVisible(true);
                        noResults.setManaged(true);
                        tabPane.setVisible(false);
                        tabPane.setManaged(false);
                    } else {
                        header.setStyle("-fx-pref-height: 30");
                        noResults.setVisible(false);
                        noResults.setManaged(false);
                        tabPane.setVisible(true);
                        tabPane.setManaged(true);
                    }
                }
            }
        });

        // Http clients types
        httpTypes.addAll(
                HttpTypes.WEBSOCKET,
                HttpTypes.HTTP_GET,
                HttpTypes.HTTP_POST
        );
        httpTypesComboBox.setItems(httpTypes);
        httpTypesComboBox.getSelectionModel().select(0);
        httpTypesComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (httpTypesComboBox.getSelectionModel().getSelectedItem() != HttpTypes.WEBSOCKET) {
                connectionButton.setText("Send Request");
            } else {
                connectionButton.setText("New Connect");
            }
            validateServerUrl();
        });

        // Set hot keys
        setHotKey(exitAppMenu, KeyCode.X);
        setHotKey(saveOutputMenu, KeyCode.S);
        setHotKey(settingsMenu, KeyCode.COMMA);

        // Connect or disconnect with server
        serverUrl.setOnKeyPressed(keyEvent -> {
            if (!connectionButton.isDisable() && keyEvent.getCode() == KeyCode.ENTER) {
                createRequest();
            }
        });
        serverUrl.textProperty().addListener((observable) -> {
            if (serverUrl.getText().length() > 0) {
                validateServerUrl();
                urlCleaner.setVisible(true);
            } else {
                connectionButton.setDisable(true);
                urlCleaner.setVisible(false);
            }
        });
    }

    /**
     * Close application with connection
     */
    @FXML
    private void exitApplication() {
        wsClients.forEach(wsClient -> {
            if (wsClient != null && wsClient.isOpenConnection()) {
                wsClient.closeConnection();
            }
        });
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void createRequest() {
        HttpTypes currentType = httpTypesComboBox.getSelectionModel().getSelectedItem();
        connectionButton.setDisable(true);
        Task task = new Task() {

            @Override
            protected Object call() throws Exception {
                setProgressVisible(true);
                if (currentType == HttpTypes.WEBSOCKET) {
                    WsMessagesTab wsClientTab = new WsMessagesTab();
                    WsClient wsClient = wsClientTab.getController().getWsClient();
                    if (wsClient.isOpenConnection()) {
                        wsClients.add(wsClient);
                        addNewTab(wsClientTab);
                    }
                } else {
                    RestTab restTab = new RestTab(httpTypesComboBox.getSelectionModel().getSelectedItem());
                    addNewTab(restTab);
                }
                setProgressVisible(false);
                return null;
            }
        };
        new Thread(task).start();
        connectionButton.setDisable(false);
    }

    /**
     * Save output message to text file
     */
    @FXML
    private void saveOutputToFile() {
        if (tabPane.isVisible()) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            StringBuilder builder = new StringBuilder();
            if (tab instanceof WsMessagesTab) {
                TabWsMessagesController controller = ((WsMessagesTab) tab).getController();
                for (ReceivedMessage message : controller.getReceivedMessageList()) {
                    builder.append(String.format(ReceivedMessageFormat.DEFAULT.getFormat().concat("\n"),
                            message.getFormattedTime(), message.getMessage()));
                }
            } else if (tab instanceof RestTab) {
                TabRestController controller = ((RestTab) tab).getController();
                builder.append(controller.getDetailNode().getText());
                builder.append(controller.getMasterNode().getText());
            }
            new FilesOperations().saveTextToFile(builder.toString());
        }
    }

    /**
     * Show settings tab
     */
    @FXML
    private void showSettings() {
        if (settingsTab == null) {
            settingsTab = new SettingsTab();
            settingsTab.setOnClosed(event -> settingsTab = null);
        }
        ObservableList<Tab> tabsList = tabPane.getTabs();
        SingleSelectionModel<Tab> selectTabModel = tabPane.getSelectionModel();
        if (!tabsList.contains(settingsTab)) {
            tabsList.add(settingsTab);
        }
        selectTabModel.select(settingsTab);
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

    @FXML
    private void clearServerUrl() {
        if (serverUrl.getText().length() > 0) {
            serverUrl.clear();
        }
    }

    /**
     * Add new client as tab with result
     *
     * @param tab Tab
     */
    private void addNewTab(Tab tab) {
        Platform.runLater(() -> {
            TabPane tabPane = getTabPane();
            SingleSelectionModel<Tab> selectTabModel = tabPane.getSelectionModel();
            tabPane.getTabs().add(tab);
            selectTabModel.select(tab);
        });
    }

    /**
     * Validate server url start with correct uri schema
     */
    private void validateServerUrl() {
        if (getHttpType() == HttpTypes.WEBSOCKET) {
            if (serverUrl.getText().startsWith("ws://") || serverUrl.getText().startsWith("wss://")) {
                connectionButton.setDisable(false);
            } else {
                connectionButton.setDisable(true);
            }
        } else {
            if (serverUrl.getText().startsWith("http://") || serverUrl.getText().startsWith("https://")) {
                connectionButton.setDisable(false);
            } else {
                connectionButton.setDisable(true);
            }
        }
    }

    /**
     * Get ws clients list
     *
     * @return List<WsClient>
     */
    public List<WsClient> getWsClients() {
        return wsClients;
    }

    /**
     * Get current selected http type
     * @return HttpTypes
     */
    HttpTypes getHttpType() {
        return httpTypesComboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * Get http headers pop over
     *
     * @return HeadersPopOver
     */
    private HeadersPopOver getHeadersPopOver() {
        if (headersPopOver == null) {
            headersPopOver = new HeadersPopOver();
        }
        return headersPopOver;
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
    public TextField getServerUrl() {
        return serverUrl;
    }

    /**
     * Get headers list
     *
     * @return ObservableList<Header>
     */
    ObservableList<Header> getHeadersList() {
        return getHeadersPopOver().getHeadersController().getHeaderObservableList();
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
     * Set hot key for menu element
     *
     * @param item    MenuItem
     * @param keyCode KeyCode
     */
    private void setHotKey(MenuItem item, KeyCode keyCode) {
        KeyCombination.Modifier key = (platform == OSX) ? KeyCombination.META_DOWN : KeyCombination.CONTROL_DOWN;
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

    /**
     * Set visible progress bar
     *
     * @param isVisible boolean
     */
    public void setProgressVisible(boolean isVisible) {
        Platform.runLater(() -> progress.setVisible(isVisible));
    }
}