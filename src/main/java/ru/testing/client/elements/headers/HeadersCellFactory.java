package ru.testing.client.elements.headers;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.common.db.objects.Header;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Header cell factory
 */
public class HeadersCellFactory extends ListCell<Header> {

    private ObservableList<Header> list;

    public HeadersCellFactory(ObservableList<Header> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(Header header, boolean empty) {
        super.updateItem(header, empty);
        if (header != null) {
            setText(String.format("%s: %s", header.getName(), header.getValue()));
            setContextMenu(getHeaderContextMenu(this));
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    /**
     * Context menu for headers view
     *
     * @return ContextMenu
     */
    private ContextMenu getHeaderContextMenu(ListCell<Header> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.clearPopOverCell(cell),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
