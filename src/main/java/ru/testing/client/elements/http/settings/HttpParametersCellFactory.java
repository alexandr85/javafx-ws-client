package ru.testing.client.elements.http.settings;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Http parameters cell factory
 */
public class HttpParametersCellFactory extends ListCell<HttpParameter> {

    private ObservableList<HttpParameter> list;

    public HttpParametersCellFactory(ObservableList<HttpParameter> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(HttpParameter parameter, boolean empty) {
        super.updateItem(parameter, empty);
        if (parameter != null) {
            setText(String.format("%s=%s", parameter.getName(), parameter.getValue()));
            setContextMenu(getHeaderContextMenu(this));
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    /**
     * Context menu for http parameters views
     *
     * @return ContextMenu
     */
    private ContextMenu getHeaderContextMenu(ListCell<HttpParameter> cell) {
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                ContextMenuItems.removeCell(cell),
                ContextMenuItems.clearListView(list)
        );
        return contextMenu;
    }
}
