package ru.testing.client.elements.message;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.ContextMenuItems;

import static ru.testing.client.elements.message.OutputMessageType.SEND;

/**
 * Output message cell factory
 */
public class OutputMessageCellFactory extends ListCell<OutputMessage> {

    private static final String SEND_MESSAGE_CSS = "message_send";
    private ObservableList<OutputMessage> list;
    private MainController main;

    public OutputMessageCellFactory(ObservableList<OutputMessage> list, MainController mainController) {
        this.list = list;
        main = mainController;
    }

    @Override
    protected void updateItem(OutputMessage item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(String.format(OutputMessageFormat.DEFAULT.getFormat(), item.getFormattedTime(), item.getMessage()));
            if (item.getMessageType() == SEND) {
                getStyleClass().add(SEND_MESSAGE_CSS);
            } else {
                getStyleClass().removeAll(SEND_MESSAGE_CSS);
            }
        } else {
            setText(null);
            setGraphic(null);
            getStyleClass().removeAll(SEND_MESSAGE_CSS);
        }
        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() >= 2) {
                    new DetailTab(item, main);
                }
            }
        });
    }

    @Override
    protected boolean isItemChanged(OutputMessage oldItem, OutputMessage newItem) {
        MultipleSelectionModel<OutputMessage> selectionModel = main.getOutputTextView().getSelectionModel();
        if (selectionModel.getSelectedItems().size() > 1) {
            final ObservableList<OutputMessage> list = selectionModel.getSelectedItems();
            if (list != null) {
                setContextMenu(getMultiContextMenu(list));
            }
        } else {
            setContextMenu(getSingleContextMenu(getItem()));
        }
        return oldItem != null ? !oldItem.equals(newItem) : newItem != null;
    }



    /**
     * Context menu for output message view
     *
     * @param item OutputMessage
     * @return ContextMenu
     */
    private ContextMenu getSingleContextMenu(final OutputMessage item) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.copyCellMessage(item),
                m.copyCellTime(item),
                m.copyCellAll(item),
                new SeparatorMenuItem(),
                m.saveMessageToFile(item, main),
                m.showMessage(item, main),
                new SeparatorMenuItem(),
                m.clearListView(list)
        );
        return contextMenu;
    }

    /**
     * Context menu for output message view
     *
     * @return ContextMenu
     */
    private ContextMenu getMultiContextMenu(final ObservableList<OutputMessage> selectedList) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.copySelected(selectedList),
                m.saveSelectedToFile(selectedList, main),
                m.saveSelectedSendToFile(selectedList, main),
                m.saveSelectedRecentToFile(selectedList, main),
                new SeparatorMenuItem(),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
