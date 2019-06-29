package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.testing.client.FXApp;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.controllers.TabRestController;

import java.io.IOException;
import java.net.URI;

/**
 * Tab with detail message
 */
public class RestTab extends Tab {

    private static final Logger LOGGER = Logger.getLogger(SettingsTab.class.getName());
    private TabRestController controller;

    public RestTab(HttpTypes httpTypes) {
        String urlInfo = FXApp.getMainController().getServerUrl().getText();

        // Setup tab tooltip
        setTooltip(new Tooltip(String.format("Response from %s", urlInfo)));
        URI uri = URI.create(urlInfo);
        urlInfo = uri.getPath();

        setText(String.format("%s %s", httpTypes.getName(), urlInfo));
        setGraphic(new ImageView("/images/message.png"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tab.rest.message.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load view form: {}", e);
        }
    }

    public TabRestController getController() {
        return controller;
    }
}
