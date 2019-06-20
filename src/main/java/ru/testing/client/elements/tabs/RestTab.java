package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.controllers.TabRestController;

import java.io.IOException;

/**
 * Tab with detail message
 */
public class RestTab extends Tab {

    private static final Logger LOGGER = Logger.getLogger(SettingsTab.class.getName());
    private TabRestController controller;

    public RestTab(HttpTypes httpTypes) {

        setText(String.format("%s result", httpTypes.getName()));

        try {
            setGraphic(new ImageView(getClass().getResource("/images/message.png").toExternalForm()));
        } catch (Exception e) {
            LOGGER.error("Image not found", e);
        }

        try {
            var loader = new FXMLLoader(getClass().getResource("/views/tab.rest.message.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load views form", e);
        }
    }

    public TabRestController getController() {
        return controller;
    }
}
