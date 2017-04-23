package ru.testing.client.websocket;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import ru.testing.client.MainApp;
import ru.testing.client.common.db.objects.ReceivedMessage;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Output message cell factory
 */
public class ReceivedMessageCellFactory extends ListCell<ReceivedMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private ObservableList<ReceivedMessage> list;
    private MainController mainController = MainApp.getMainController();

    public ReceivedMessageCellFactory(ObservableList<ReceivedMessage> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(ReceivedMessage item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(String.format(ReceivedMessageFormat.DEFAULT.getFormat(), item.getFormattedTime(), item.getMessage()));
            if (item.getMessageType() == ReceivedMessageType.SEND) {
                getStyleClass().add(SEND_MESSAGE_CSS);
            } else {
                getStyleClass().removeAll(SEND_MESSAGE_CSS);
            }
            setContextMenu(getContextMenu(item));
        } else {
            setText(null);
            setGraphic(null);
            getStyleClass().removeAll(SEND_MESSAGE_CSS);
        }
    }

    @Override
    protected boolean isItemChanged(ReceivedMessage oldItem, ReceivedMessage newItem) {
//        MultipleSelectionModel<ReceivedMessage> selectionModel = mainController.getOutputTextView().getSelectionModel();
//        setOnMouseClicked(event -> {
//            if (event.getButton().equals(MouseButton.SECONDARY)) {
//                selectionModel.clearSelection();
//                selectionModel.select(getItem());
//            }
//            if (event.getButton().equals(MouseButton.PRIMARY)) {
//                if (event.getClickCount() >= 2) {
//                    new WsMessageTab(getItem(), mainController);
//                }
//            }
//        });
        return oldItem != null ? !oldItem.equals(newItem) : newItem != null;
    }

    /**
     * Context menu for output message view
     *
     * @param item ReceivedMessage
     * @return ContextMenu
     */
    private ContextMenu getContextMenu(final ReceivedMessage item) {
        final ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.copyCellMessage(item),
                m.copyCellTime(item),
                m.copyCellAll(item),
                new SeparatorMenuItem(),
                m.saveMessageToFile(item, mainController),
                m.showMessage(item),
                new SeparatorMenuItem(),
//                m.deselectCell(mainController.getOutputTextView()),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
