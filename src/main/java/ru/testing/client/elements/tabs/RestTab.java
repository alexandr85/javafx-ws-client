package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.MainApp;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.settings.SettingsTab;

import java.io.IOException;

/**
 * Tab with detail message
 */
public class RestTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());

    public RestTab(HttpTypes httpTypes) {
        this.setText(String.format("%s response", httpTypes));
        this.setGraphic(new ImageView("/images/message.png"));

        // Load detail message view form
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.rest.message.fxml"));
            Parent root = loader.load();
            this.setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load view form: {}", e.getMessage());
        }

        // Setup new tab with content in tabPane
        TabPane tabPane = MainApp.getMainController().getTabPane();
        SingleSelectionModel<Tab> selectTabModel = tabPane.getSelectionModel();
        tabPane.getTabs().add(this);
        selectTabModel.select(this);
        MainController mainController = MainApp.getMainController();
        this.setTooltip(new Tooltip(String.format("Response from %s", mainController.getServerUrl().getText())));
    }
}
