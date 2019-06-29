package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Tab with application settings form
 */
public class SettingsTab extends Tab {

    private static final Logger LOGGER = Logger.getLogger(SettingsTab.class.getName());

    public SettingsTab() {

        // Create new tab
        setText("Settings");
        setTooltip(new Tooltip("Application settings"));
        setGraphic(new ImageView("/images/settings.png"));

        // Load settings view form
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/tab.settings.fxml"));
            Parent root = loader.load();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error(String.format("Error load settings tab: %s", e.getMessage()));
        }
    }
}
