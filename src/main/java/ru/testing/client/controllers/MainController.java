package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import org.apache.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.http.settings.HttpSettingsPopOver;
import ru.testing.client.elements.tabs.*;
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

    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    private final ObservableList<HttpTypes> httpTypes = FXCollections.observableArrayList();
    private final org.controlsfx.tools.Platform platform = org.controlsfx.tools.Platform.getCurrent();
    private final KeyCombination.Modifier keyModifier = (platform == OSX)
            ? KeyCombination.META_DOWN : KeyCombination.CONTROL_DOWN;

    private List<WsClient> wsClients = new ArrayList<>();
    private AppProperties properties = AppProperties.getInstance();
    private NewClientTab newClientTab = new NewClientTab();
    private HttpSettingsPopOver httpSettingsPopOver;
    private SettingsTab settingsTab;

    @FXML
    private MenuBar menuBar;
    @FXML
    private TilePane connectTilePane;
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
    private MenuItem nextTab;
    @FXML
    private MenuItem prevTab;
    @FXML
    private MenuItem closeTab;
    @FXML
    private MenuItem closeAllTabs;
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
        tabPane.getTabs().add(newClientTab);
        tabPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
            if (c.next()) {
                final var header = (StackPane) tabPane.lookup(".tab-header-area");
                if (header != null) {
                    if (tabPane.getTabs().size() == 1) {
                        header.setStyle("-fx-pref-height: 0");
                    } else {
                        header.setStyle("-fx-pref-height: 30");
                    }
                }
            }
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            var httpController = getHttpSettingsPopOver().getHttpSettingsController();

            // Save inserted last values in 'new' tab
            if (oldValue instanceof NewClientTab) {
                newClientTab.setPrevType(httpTypesComboBox.getSelectionModel().getSelectedItem());
                newClientTab.setPrevHeaders(httpController.getHeadersList());
                newClientTab.setPrevHttpParams(httpController.getHttpParametersList());
                newClientTab.setPrevUrl(serverUrl.getText());
            }

            // Set data for new tab
            if (newValue instanceof NewClientTab) {
                connectTilePane.setDisable(false);
                httpTypesComboBox.setDisable(false);
                httpSettings.setDisable(false);
                serverUrl.setEditable(true);
                urlCleaner.setDisable(false);
                updateConnectionButtonName();

                // Set previous data
                httpTypesComboBox.getSelectionModel().select(newClientTab.getPrevType());
                httpController.getHeadersList().clear();
                httpController.getHeadersList().addAll(newClientTab.getPrevHeaders());
                httpController.getHttpParametersList().clear();
                httpController.getHttpParametersList().addAll(newClientTab.getPrevHttpParams());
                serverUrl.setText(newClientTab.getPrevUrl());
            } else if (newValue instanceof SettingsTab || newValue instanceof WsMessageTab) {
                connectTilePane.setDisable(true);
            } else if (newValue instanceof WsMessagesTab) {
                connectTilePane.setDisable(false);
                httpTypesComboBox.setDisable(true);
                httpTypesComboBox.getSelectionModel().select(HttpTypes.WEBSOCKET);
                httpSettings.setDisable(true);
                urlCleaner.setDisable(true);

                // Set ws client data
                ((WsMessagesTab) newValue).getController().checkConnectionStatus();
                serverUrl.setText(((WsMessagesTab) newValue).getServerUrl());
                serverUrl.setEditable(false);
            } else if (newValue instanceof RestTab) {
                connectTilePane.setDisable(false);
                httpTypesComboBox.setDisable(true);
                httpSettings.setDisable(false);
                connectionButton.setDisable(false);

                // Set request data
                var restController = ((RestTab) newValue).getController();
                serverUrl.setText(restController.getServerUrl());
                httpTypesComboBox.getSelectionModel().select(restController.getHttpType());
                httpController.getHeadersList().clear();
                httpController.getHeadersList().addAll(restController.getHeaders());
                httpController.getHttpParametersList().clear();
                httpController.getHttpParametersList().addAll(restController.getParameters());
                connectionButton.setText("Send");
            }
            tabPane.getSelectionModel().select(newValue);
        });

        // Http clients types
        httpTypes.addAll(HttpTypes.values());
        httpTypesComboBox.setItems(httpTypes);
        httpTypesComboBox.getSelectionModel().select(0);
        httpTypesComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            var controller = getHttpSettingsPopOver().getHttpSettingsController();
            var paramsPane = controller.getParametersPane();
            var headersPane = controller.getHeadersPane();
            var tpParameters = controller.getTpParameters();
            var fpParameters = controller.getFpParameters();
            var bodyArea = controller.getBodyTextArea();

            switch (newValue) {
                case HTTP_GET:
                    headersPane.setCollapsible(true);
                    paramsPane.setVisible(true);
                    paramsPane.setManaged(true);
                    tpParameters.setVisible(true);
                    tpParameters.setManaged(true);
                    fpParameters.setVisible(true);
                    fpParameters.setManaged(true);
                    bodyArea.setVisible(false);
                    bodyArea.setManaged(false);
                    paramsPane.setText("Parameters");
                    break;
                case HTTP_POST:
                    headersPane.setCollapsible(true);
                    paramsPane.setVisible(true);
                    paramsPane.setManaged(true);
                    tpParameters.setVisible(false);
                    tpParameters.setManaged(false);
                    fpParameters.setVisible(false);
                    fpParameters.setManaged(false);
                    bodyArea.setVisible(true);
                    bodyArea.setManaged(true);
                    paramsPane.setText("Body");
                    break;
                default:
                    controller.getAccordion().setExpandedPane(headersPane);
                    headersPane.setCollapsible(false);
                    paramsPane.setVisible(false);
                    paramsPane.setManaged(false);
            }

            updateConnectionButtonName();
            validateServerUrl();
        });

        // Set hot keys
        setHotKey(closeTab, KeyCode.W);
        setHotKey(closeAllTabs, KeyCode.W, KeyCombination.SHIFT_DOWN);
        setHotKey(nextTab, KeyCode.RIGHT);
        setHotKey(prevTab, KeyCode.LEFT);
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
        var currentType = httpTypesComboBox.getSelectionModel().getSelectedItem();
        var task = new Task() {

            @Override
            protected Object call() {
                connectionButton.setDisable(true);
                setProgressVisible(true);
                var currentTab = tabPane.getSelectionModel().getSelectedItem();

                if (currentTab instanceof NewClientTab) {
                    switch (currentType) {
                        case WEBSOCKET:
                            LOGGER.debug("Create websocket connection");
                            var wsClientTab = new WsMessagesTab();
                            var wsClient = wsClientTab.getController().getWsClient();
                            if (wsClient.isOpenConnection()) {
                                wsClients.add(wsClient);
                                addNewTab(wsClientTab);
                            }
                            break;
                        case HTTP_GET:
                        case HTTP_POST:
                            LOGGER.debug("Create rest request");
                            var restTab = new RestTab(httpTypesComboBox.getSelectionModel().getSelectedItem());
                            addNewTab(restTab);
                    }
                } else if (currentTab instanceof WsMessagesTab) {
                    var controller = ((WsMessagesTab) currentTab).getController();
                    var wsClient = controller.getWsClient();
                    if (!wsClient.isOpenConnection()) {
                        controller.startWsClient();
                        controller.checkConnectionStatus();
                    } else {
                        wsClient.closeConnection();
                    }
                } else if (currentTab instanceof RestTab) {
                    var rest = ((RestTab) currentTab).getController();
                    Platform.runLater(rest::execute);
                }

                setProgressVisible(false);
                connectionButton.setDisable(false);
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Save output message to text file
     */
    @FXML
    private void saveOutputToFile() {
        if (tabPane.isVisible()) {
            var tab = tabPane.getSelectionModel().getSelectedItem();
            var builder = new StringBuilder();
            if (tab instanceof WsMessagesTab) {
                var controller = ((WsMessagesTab) tab).getController();
                for (ReceivedMessage message : controller.getReceivedMessageList()) {
                    builder.append(String.format(ReceivedMessageFormat.DEFAULT.getFormat().concat("\n"),
                            message.getFormattedTime(), message.getMessage()));
                }
            } else if (tab instanceof RestTab) {
                var controller = ((RestTab) tab).getController();
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
        var tabsList = tabPane.getTabs();
        var selectTabModel = tabPane.getSelectionModel();
        if (!tabsList.contains(settingsTab)) {
            tabsList.add(settingsTab);
        }
        selectTabModel.select(settingsTab);
    }

    /**
     * Get headers pop over
     */
    @FXML
    private void showHttpSettingsPopOver() {
        if (httpSettings.isSelected()) {
            getHttpSettingsPopOver().show(httpSettings, -4);
        } else {
            getHttpSettingsPopOver().hide();
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
     * Clear server url input
     */
    @FXML
    private void clearServerUrl() {
        if (serverUrl.getText().length() > 0) {
            serverUrl.clear();
        }
    }

    @FXML
    private void nextTab() {
        if (!serverUrl.isFocused()) {
            var currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            if (currentIndex < tabPane.getTabs().size()) {
                tabPane.getSelectionModel().select(currentIndex + 1);
            }
        }
    }

    @FXML
    private void previousTab() {
        if (!serverUrl.isFocused()) {
            var currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            if (currentIndex != 0) {
                tabPane.getSelectionModel().select(currentIndex - 1);
            }
        }
    }

    @FXML
    private void closeTab() {
        var currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (!(currentTab instanceof NewClientTab)) {
            if (currentTab instanceof WsMessagesTab) {
                var wsClient = ((WsMessagesTab) currentTab).getController().getWsClient();
                wsClient.closeConnection();
            }
            tabPane.getTabs().remove(currentTab);
        }
    }

    @FXML
    private void closeTabs() {
        var tabs = tabPane.getTabs().filtered(tab -> !(tab instanceof NewClientTab));
        for (Tab tab : tabs) {
            if (tab instanceof WsMessagesTab) {
                var wsClient = ((WsMessagesTab) tab).getController().getWsClient();
                wsClient.closeConnection();
            }
        }
        tabPane.getTabs().removeAll(tabs);
    }

    /**
     * Add new client as tab with result
     *
     * @param tab Tab
     */
    private void addNewTab(Tab tab) {
        Platform.runLater(() -> {
            var tabPane = getTabPane();
            var selectTabModel = tabPane.getSelectionModel();
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
     *
     * @return HttpTypes
     */
    HttpTypes getHttpType() {
        return httpTypesComboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * Get http headers pop over
     *
     * @return HttpSettingsPopOver
     */
    private HttpSettingsPopOver getHttpSettingsPopOver() {
        if (httpSettingsPopOver == null) {
            httpSettingsPopOver = new HttpSettingsPopOver();
        }
        return httpSettingsPopOver;
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
     * Get connection button
     *
     * @return Button
     */
    Button getConnectionButton() {
        return connectionButton;
    }

    /**
     * Get headers list
     *
     * @return ObservableList<Header>
     */
    ObservableList<Header> getHeadersList() {
        return getHttpSettingsPopOver().getHttpSettingsController().getHeadersList();
    }

    /**
     * Get http parameters list
     *
     * @return ObservableList<HttpParameter>
     */
    ObservableList<HttpParameter> getHttpParametersList() {
        return getHttpSettingsPopOver().getHttpSettingsController().getHttpParametersList();
    }

    String getPostBody() {
        return getHttpSettingsPopOver().getHttpSettingsController().getBodyTextArea().getText();
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
     * Set connection button name
     */
    private void updateConnectionButtonName() {
        if (httpTypesComboBox.getSelectionModel().getSelectedItem() != HttpTypes.WEBSOCKET) {
            connectionButton.setText("New Request");
        } else {
            connectionButton.setText("New Connect");
        }
    }

    /**
     * Set hot key for menu element
     *
     * @param item    MenuItem
     * @param keyCode KeyCode
     */
    private void setHotKey(MenuItem item, KeyCode keyCode) {
        item.setAccelerator(new KeyCodeCombination(keyCode, keyModifier));
    }

    /**
     * Set hot key for menu element with shift
     *
     * @param item    MenuItem
     * @param keyCode KeyCode
     */
    private void setHotKey(MenuItem item, KeyCode keyCode, KeyCombination.Modifier keyMod) {
        item.setAccelerator(new KeyCodeCombination(keyCode, keyMod, keyModifier));
    }

    /**
     * Go to desktop browser page action
     *
     * @param url String
     */
    private void goToWebPage(String url) {
        var desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL(url).toURI());
            } catch (IOException | URISyntaxException e) {
                LOGGER.error("Error go to web page", e);
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