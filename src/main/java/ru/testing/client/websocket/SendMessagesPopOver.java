package ru.testing.client.websocket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.SendMessagesController;

import java.io.IOException;

/**
 * Send message history pop over
 */
public class SendMessagesPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessagesPopOver.class);
    private SendMessagesController controller;

    public SendMessagesPopOver() {

        // Pop over settings
        setDetachable(false);
        setArrowLocation(ArrowLocation.TOP_RIGHT);
//        setOnHidden(event -> getMainController().getSendMsgHistoryBtn().setSelected(false));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popover.send.messages.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            setContentNode(root);
        } catch (IOException e) {
            LOGGER.error("Error load sent messages pop over: {}", e.getMessage());
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
