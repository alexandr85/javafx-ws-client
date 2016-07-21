package ru.testing.client.elements.autosend;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.common.db.objects.Session;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Sessions cell factory
 */
public class AutoSendCellFactory extends ListCell<String> {

    private MainController mainController;

    public AutoSendCellFactory(MainController mainController) {
        this.mainController = mainController;
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
     * Context menu for sessions
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getContextMenu(ListCell<String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(

        );
        return contextMenu;
    }

    /**
     * Context menu for default session
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getDefaultContextMenu(ListCell<Session> cell) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                new ContextMenuItems().loadSession(cell, mainController)
        );
        return contextMenu;
    }
}
