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
 * Tab for create new client instance
 */
public class NewClientTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());

    public NewClientTab() {
        setClosable(false);
        setTooltip(new Tooltip("Create new client instance"));
        setText("NEW");
        ImageView image = new ImageView("/images/add.png");
        image.setFitHeight(20.0);
        image.setFitWidth(20.0);
        setGraphic(image);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.new.client.fxml"));
            Parent root = loader.load();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load view form: {}", e);
        }
    }
}
