package ru.testing.client.elements.message;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Output message cell factory
 */
public class OutputMessageCellFactory extends ListCell<OutputMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private ObservableList<OutputMessage> list;

    public OutputMessageCellFactory(ObservableList<OutputMessage> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(OutputMessage message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(String.format(OutputMessageFormat.DEFAULT.getFormat(), message.getFormattedTime(), message.getMessage()));
            if (message.getMessageType() == OutputMessageType.SEND) {
                getStyleClass().add(SEND_MESSAGE_CSS);
            } else {
                getStyleClass().removeAll(SEND_MESSAGE_CSS);
            }
            setContextMenu(getOutputContextMenu(this));
        } else {
            setText(null);
            setGraphic(null);
            getStyleClass().removeAll(SEND_MESSAGE_CSS);
        }
    }

    /**
     * Context menu for output message view
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getOutputContextMenu(ListCell<OutputMessage> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.getCopyCellAll(cell),
                m.getCopyCellMessage(cell),
                m.getCopyCellTime(cell),
                m.saveMessageToFile(cell),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
