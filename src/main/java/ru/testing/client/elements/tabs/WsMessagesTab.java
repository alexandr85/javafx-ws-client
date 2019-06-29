package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.testing.client.FXApp;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.TabWsMessagesController;

import java.io.IOException;

/**
 * Tab with detail message
 */
public class WsMessagesTab extends Tab {

    private static final Logger LOGGER = Logger.getLogger(SettingsTab.class.getName());
    private TabWsMessagesController controller;
    private String serverUrl;

    public WsMessagesTab() {
        MainController mainController = FXApp.getMainController();
        serverUrl = mainController.getServerUrl().getText();
        setText("WsMessages");
        setGraphic(new ImageView("/images/messages.png"));

        // Load detail message view form
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tab.ws.messages.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error(String.format("Error load view form: %s", e.getMessage()));
        }

        // Setup tab tooltip
        setTooltip(new Tooltip(String.format("Connected to %s", serverUrl)));
    }

    public TabWsMessagesController getController() {
        return controller;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
