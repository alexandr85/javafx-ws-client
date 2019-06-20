package ru.testing.client.elements.tabs;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;
import ru.testing.client.MainApp;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.controllers.TabWsMessageController;

import java.io.IOException;

/**
 * Tab with detail message
 */
public class WsMessageTab extends Tab {

    private static final Logger LOGGER = Logger.getLogger(SettingsTab.class.getName());

    public WsMessageTab(final ReceivedMessage item) {
        var mainController = MainApp.getMainController();
        ObservableList<Tab> tabsList = mainController.getTabPane().getTabs();
        if (item != null) {

            // Setup tab
            setText("WS Message");

            try {
                setGraphic(new ImageView(getClass().getResource("/images/message.png").toExternalForm()));
            } catch (Exception e) {
                LOGGER.error("Image not found", e);
            }

            // Load detail message views form
            try {
                var loader = new FXMLLoader(getClass().getResource("/views/tab.ws.message.fxml"));
                Parent root = loader.load();
                TabWsMessageController controller = loader.getController();
                controller.setMessage(item);
                setContent(root);
            } catch (IOException e) {
                LOGGER.error("Error load views form", e);
            }

            // Setup new tab with content in tabPane
            SingleSelectionModel<Tab> selectTabModel = mainController.getTabPane().getSelectionModel();
            tabsList.add(this);
            selectTabModel.select(this);
        }
    }
}
