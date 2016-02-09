package ru.testing.client.websocket;

import javafx.collections.ObservableList;
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
     * Console message handler
     */
    public MessageHandler() {
        this.mainController = null;
    }

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
            ObservableList<String> filterList = mainController.getFilterList();
            if (mainController.getFilterStatus() && filterList.size() > 0) {
                for (String filterItem : filterList) {
                    if (message.contains(filterItem)) {
                        mainController.addMessageToOutput(RECEIVED, message);
                        break;
                    }
                }
            } else {
                mainController.addMessageToOutput(RECEIVED, message);
            }
        } else {
            LOGGER.info("Response: {}", message);
        }
    }
}
