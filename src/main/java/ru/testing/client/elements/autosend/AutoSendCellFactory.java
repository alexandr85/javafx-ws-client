package ru.testing.client.elements.autosend;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Auto send messages cell factory
 */
public class AutoSendCellFactory extends ListCell<String> {

    private ObservableList<String> list;

    public AutoSendCellFactory(ObservableList<String> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(String message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(message);
            setContextMenu(getContextMenu(this));
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    /**
     * Context menu for auto messages
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getContextMenu(ListCell<String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.clearPopOverCell(cell),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
