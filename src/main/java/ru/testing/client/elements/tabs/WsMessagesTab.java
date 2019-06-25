package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import ru.testing.client.FXApp;
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
        var mainController = FXApp.getMainController();
        serverUrl = mainController.getServerUrl().getText();

        setText("WsMessages");
        setOnClosed(event -> {
            var wsClient = controller.getWsClient();
            if (wsClient != null) {
                wsClient.closeConnection();
                mainController.getWsClients().remove(wsClient);
            }
        });

        try {
            setGraphic(new ImageView(getClass().getResource("/images/messages.png").toExternalForm()));
        } catch (Exception e) {
            LOGGER.error("Image not found", e);
        }

        // Load detail message views form
        try {
            var loader = new FXMLLoader(getClass().getResource("/views/tab.ws.messages.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load views form", e);
        }

        // Setup tab tooltip
        var tp = new Tooltip(String.format("Connected to %s", serverUrl));
        tp.setShowDelay(new Duration(10));
        setTooltip(tp);
    }

    public TabWsMessagesController getController() {
        return controller;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
