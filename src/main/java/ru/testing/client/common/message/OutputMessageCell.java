package ru.testing.client.common.message;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.testing.client.tools.ContextMenuItems;

/**
 * Output message cell factory
 */
public class OutputMessageCell extends ListCell<OutputMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private static final String IMAGES_SEND = "/images/arrow-up.png";
    private static final String IMAGES_RECEIVED = "/images/arrow-down.png";
    private ImageView imageView = new ImageView();
    private ObservableList<OutputMessage> list;

    public OutputMessageCell(ObservableList<OutputMessage> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(OutputMessage message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(String.format(OutputMessageFormat.DEFAULT.getFormat(), message.getFormattedTime(), message.getMessage()));
            String imageUrl;
            if (message.getMessageType() == OutputMessageType.SEND) {
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
