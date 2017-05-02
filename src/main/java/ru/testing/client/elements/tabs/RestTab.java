package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.MainApp;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.controllers.TabRestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tab with detail message
 */
public class RestTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());
    private TabRestController controller;

    public RestTab(HttpTypes httpTypes) {
        String urlInfo = MainApp.getMainController().getServerUrl().getText();

        // Setup tab tooltip
        setTooltip(new Tooltip(String.format("Response from %s", urlInfo)));
        try {
            URI uri = new URI(urlInfo);
            urlInfo = uri.getPath();
        } catch (URISyntaxException e) {
            LOGGER.error("Error get uri", e.getMessage());
        }
        setText(String.format("%s %s", httpTypes.getName(), urlInfo));
        setGraphic(new ImageView("/images/message.png"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.rest.message.fxml"));
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
