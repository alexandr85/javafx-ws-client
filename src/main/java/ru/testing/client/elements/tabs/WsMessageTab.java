package ru.testing.client.elements.tabs;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.MainApp;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.controllers.TabWsMessageController;
import ru.testing.client.controllers.MainController;

import java.io.IOException;

/**
 * Tab with detail message
 */
public class WsMessageTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());

    public WsMessageTab(final ReceivedMessage item) {
        MainController mainController = MainApp.getMainController();
        ObservableList<Tab> tabsList = mainController.getTabPane().getTabs();
        if (item != null) {

            // Setup tab
            this.setText("WS Message");
            this.setGraphic(new ImageView("/images/message.png"));

            // Load detail message view form
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.ws.message.fxml"));
                Parent root = loader.load();
                TabWsMessageController controller = loader.getController();
                controller.setMessage(item);
                this.setContent(root);
            } catch (IOException e) {
                LOGGER.error("Error load view form: {}", e.getMessage());
            }

            // Setup new tab with content in tabPane
            SingleSelectionModel<Tab> selectTabModel = mainController.getTabPane().getSelectionModel();
            tabsList.add(this);
            selectTabModel.select(this);
        }
    }
}
