package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tab with application settings form
 */
public class SettingsTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());

    public SettingsTab() {

        // Create new tab
        setText("Settings");
        setTooltip(new Tooltip("Application settings"));
        setGraphic( new ImageView("/images/settings.png"));

        // Load settings view form
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.settings.fxml"));
            Parent root = loader.load();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load settings tab: {}", e.getMessage());
        }
    }
}
