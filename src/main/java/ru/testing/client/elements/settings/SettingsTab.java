package ru.testing.client.elements.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.TabSettingsController;

import java.io.IOException;

/**
 * Tab with application settings form
 */
public class SettingsTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());

    public SettingsTab(MainController mainController) {

        // Create new tab
        this.setText("Settings");
        this.setTooltip(new Tooltip("Application settings"));
        this.setGraphic( new ImageView("/images/settings.png"));

        // Load settings view form
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.settings.fxml"));
            loader.setController(new TabSettingsController(mainController));
            Parent root = loader.load();
            this.setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load settings tab: {}", e.getMessage());
        }
    }
}
