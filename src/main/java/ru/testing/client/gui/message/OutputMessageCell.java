package ru.testing.client.gui.message;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.testing.client.commons.MessageType;
import ru.testing.client.commons.OutputFormat;
import ru.testing.client.gui.tools.ContextMenuItems;

/**
 * Output message cell factory
 */
public class OutputMessageCell extends ListCell<OutputMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private static final String IMAGES_SEND = "/images/arrow-up.png";
    private static final String IMAGES_RECEIVED = "/images/arrow-down.png";
    private ImageView imageView = new ImageView();
    private ListView listView;

    public OutputMessageCell(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected void updateItem(OutputMessage message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(String.format(OutputFormat.DEFAULT.getFormat(), message.getFormattedTime(), message.getMessage()));
            String imageUrl;
            if (message.getMessageType() == MessageType.SEND) {
                getStyleClass().add(SEND_MESSAGE_CSS);
                imageUrl = IMAGES_SEND;
            } else {
                getStyleClass().removeAll(SEND_MESSAGE_CSS);
                imageUrl = IMAGES_RECEIVED;
            }
            imageView.setImage(new Image(getClass().getResource(imageUrl).toExternalForm(), false));
            setGraphic(imageView);
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
                m.saveMessageToFile(cell),
                m.clearListView(listView)
        );
        return contextMenu;
    }
}
