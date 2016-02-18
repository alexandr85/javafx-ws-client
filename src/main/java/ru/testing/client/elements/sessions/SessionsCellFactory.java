package ru.testing.client.elements.sessions;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import ru.testing.client.common.db.objects.Session;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;
import ru.testing.client.elements.ContextMenuItems;

/**
 * Sessions cell factory
 */
public class SessionsCellFactory extends ListCell<Session> {

    private static final String DEFAULT_SESSION_NAME = "default";
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
            setText(session.getName());
            if (session.getName().equals(DEFAULT_SESSION_NAME)) {
                setContextMenu(getDefaultContextMenu(this));
            } else {
                setContextMenu(getContextMenu(this));
            }
            setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    mainController.setDataFromSession(session.getId());
                    mainController.getSessionsPopOver().hide();
                }
            });
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
    private ContextMenu getContextMenu(ListCell<Session> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.loadSession(cell, mainController),
                m.deleteSessionMenu(cell, sessionsController)
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
