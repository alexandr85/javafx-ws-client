package ru.testing.client.elements.message;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.SendMessagesController;
import ru.testing.client.controllers.MainController;

import java.io.IOException;

/**
 * Send message history pop over
 */
public class SendMessagesPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessagesPopOver.class);
    private SendMessagesController controller;

    public SendMessagesPopOver(MainController main) {

        // Pop over settings
        setDetachable(false);
        setArrowLocation(ArrowLocation.TOP_RIGHT);
        setOnHidden(event -> main.getSendMsgHistoryBtn().setSelected(false));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popover.send.messages.fxml"));
            controller = new SendMessagesController(main);
            loader.setController(controller);
            Parent root = loader.load();
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
