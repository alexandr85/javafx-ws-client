package ru.testing.client.common.message;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.tools.ContextMenuItems;

/**
 * Filter cell factory
 */
public class FilterCellFactory extends ListCell<String> {

    private ObservableList<String> list;

    public FilterCellFactory(ObservableList<String> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(String message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(message);
            setContextMenu(getFilterContextMenu(this));
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    /**
     * Context menu for filter pop over
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getFilterContextMenu(ListCell<String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.clearPopOverCell(cell),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
