package ru.testing.client.elements.sessions;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Sessions cell factory
 */
public class SessionsCellFactory extends ListCell<Session> {

    private SessionsController sessionsController;
    private MainController mainController;

    public SessionsCellFactory(SessionsController sessionsController, MainController mainController) {
        this.sessionsController = sessionsController;
        this.mainController = mainController;
    }

    @Override
    protected void updateItem(Session session, boolean empty) {
        super.updateItem(session, empty);
        if (session != null) {
            setText(String.format("%s", session.getName()));
            setContextMenu(getSessionContextMenu(this));
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    /**
     * Context menu for history pop over
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getSessionContextMenu(ListCell<Session> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.loadSession(cell, mainController),
                m.deleteSession(cell, sessionsController)
        );
        return contextMenu;
    }
}
