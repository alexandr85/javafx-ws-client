package ru.testing.client.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.MainController;

import static ru.testing.client.elements.message.OutputMessageType.RECEIVED;

/**
 * FX output message handler
 */
public class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger("MessageHandler");
    private MainController mainController;

    /**
     * JavaFx message handler
     *
     * @param mainController MainController
     */
    public MessageHandler(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void onMessage(String message) {
        if (mainController != null) {
            mainController.addMessageToOutput(RECEIVED, message);
        } else {
            LOGGER.info("Response: {}\n", message);
        }
    }
}
