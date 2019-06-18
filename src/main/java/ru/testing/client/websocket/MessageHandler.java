package ru.testing.client.websocket;

import org.apache.log4j.Logger;
import ru.testing.client.controllers.TabWsMessagesController;

import static ru.testing.client.websocket.ReceivedMessageType.RECEIVED;

/**
 * FX output message handler
 */
public class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    private static final Logger LOGGER = Logger.getLogger("MessageHandler");
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
            LOGGER.debug(String.format("Response: %s",message));
        }
    }
}
