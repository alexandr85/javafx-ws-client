package ru.testing.client.message;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ru.testing.client.gui.ContextMenuItems;

/**
 * Output message cell factory
 */
public class OutputMessageCell extends ListCell<OutputMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private ListView listView;

    public OutputMessageCell(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected void updateItem(OutputMessage message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(String.format("%-12s %s", message.getFormattedTime(), message.getMessage()));
            if (message.getMessageType() == MessageType.SEND) {
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
     * Context menu for history pop over
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
                m.clearListView(listView)
        );
        return contextMenu;
    }
}
