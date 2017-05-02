package ru.testing.client.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.TabWsMessagesController;

import static ru.testing.client.websocket.ReceivedMessageType.RECEIVED;

/**
 * FX output message handler
 */
public class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger("MessageHandler");
    private TabWsMessagesController messagesController;

    /**
     * JavaFx message handler
     *
     * @param messagesController TabWsMessagesController
     */
    public MessageHandler(TabWsMessagesController messagesController) {
        this.messagesController = messagesController;
    }

    @Override
    public void onMessage(String message) {
        if (messagesController != null) {
            messagesController.addMessageToOutput(RECEIVED, message);
        } else {
            LOGGER.info("Response: {}\n", message);
        }
    }
}
