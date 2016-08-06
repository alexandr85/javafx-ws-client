package ru.testing.client.elements.message;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.DetailMsgController;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.settings.SettingsTab;

import java.io.IOException;

/**
 * Tab with detail message
 */
public class DetailTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());

    public DetailTab(final OutputMessage item, MainController main) {
        ObservableList<Tab> tabsList = main.getTabPane().getTabs();
        if (item != null) {

            // Setup tab
            MultipleSelectionModel<OutputMessage> selectionModel = main.getOutputTextView().getSelectionModel();
            this.setText(String.format("Message #%s", selectionModel.getSelectedIndex() + 1));
            this.setGraphic(new ImageView("/images/message.png"));

            // Load detail message view form
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/detail.fxml"));
                loader.setController(new DetailMsgController(item, main));
                Parent root = loader.load();
                this.setContent(root);
            } catch (IOException e) {
                LOGGER.error("Error load detail message form: {}", e.getMessage());
            }

            // Setup new tab with content in tabPane
            SingleSelectionModel<Tab> selectTabModel = main.getTabPane().getSelectionModel();
            tabsList.add(this);
            selectTabModel.select(this);
        }
    }
}
