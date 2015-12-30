package ru.testing.client.websocket;

import javafx.collections.ObservableList;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.message.OutputMessageType;

import javax.websocket.MessageHandler;

/**
 * FX output message handler
 */
public class FXMessageHandler implements MessageHandler.Whole<String> {

    private MainController mainController;

    public FXMessageHandler(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void onMessage(String message) {
        ObservableList<String> filterList = mainController.getFilterList();
        if (mainController.getFilterStatus() && filterList.size() > 0) {
            for (String filterItem : filterList) {
                if (message.contains(filterItem)) {
                    mainController.addMessageToOutput(OutputMessageType.RECEIVED, message);
                    break;
                }
            }
        } else {
            mainController.addMessageToOutput(OutputMessageType.RECEIVED, message);
        }
    }
}
