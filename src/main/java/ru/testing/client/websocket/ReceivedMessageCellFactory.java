package ru.testing.client.websocket;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.controllers.TabWsMessagesController;
import ru.testing.client.elements.ContextMenuItems;
import ru.testing.client.elements.tabs.WsMessageTab;

import java.util.Objects;

/**
 * Output message cell factory
 */
public class ReceivedMessageCellFactory extends ListCell<ReceivedMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private ObservableList<ReceivedMessage> list;
    private TabWsMessagesController controller;

    public ReceivedMessageCellFactory(TabWsMessagesController controller, boolean isFiltered) {
        this.controller = controller;
        if (isFiltered) {
            this.list = controller.getReceivedFilteredMessageList();
        } else {
            this.list = controller.getReceivedMessageList();
        }
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
        MultipleSelectionModel<ReceivedMessage> selectionModel = controller.getOutputTextView().getSelectionModel();
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                selectionModel.clearSelection();
                selectionModel.select(getItem());
            }
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() >= 2) {
                    new WsMessageTab(getItem());
                }
            }
        });
        return !Objects.equals(oldItem, newItem);
    }

    /**
     * Context menu for output message views
     *
     * @param item ReceivedMessage
     * @return ContextMenu
     */
    private ContextMenu getContextMenu(final ReceivedMessage item) {
        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                ContextMenuItems.copyCellMessage(item),
                ContextMenuItems.copyCellTime(item),
                ContextMenuItems.copyCellAll(item),
                new SeparatorMenuItem(),
                ContextMenuItems.saveMessageToFile(item),
                ContextMenuItems.showMessage(item),
                new SeparatorMenuItem(),
                ContextMenuItems.deselectCell(controller.getOutputTextView()),
                ContextMenuItems.clearListView(list)
        );
        return contextMenu;
    }
}
