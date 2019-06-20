package ru.testing.client.websocket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.log4j.Logger;
import org.controlsfx.control.PopOver;
import ru.testing.client.controllers.SendMessagesController;
import ru.testing.client.controllers.TabWsMessagesController;

import java.io.IOException;

/**
 * Send message history pop over
 */
public class SendMessagesPopOver extends PopOver {

    private static final Logger LOGGER = Logger.getLogger(SendMessagesPopOver.class);
    private SendMessagesController controller;

    public SendMessagesPopOver(TabWsMessagesController tabWsMessagesController) {

        // Pop over settings
        setDetachable(false);
        setArrowLocation(ArrowLocation.TOP_RIGHT);
        setOnHidden(event -> tabWsMessagesController.getSendMsgHistoryBtn().setSelected(false));

        try {
            var loader = new FXMLLoader(getClass().getResource("/views/popover.send.messages.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.setCheckListViewCellFactory(tabWsMessagesController);
            setContentNode(root);
        } catch (IOException e) {
            LOGGER.error("Error load sent messages pop over", e);
        }
    }

    /**
     * Get sent message controller
     *
     * @return SendMessagesController
     */
    public SendMessagesController getController() {
        return controller;
    }
}
