package ru.testing.client.elements.sessions;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;

/**
 * Sessions cell factory
 */
public class SessionsCellFactory extends ListCell<String> {

    private ObservableList<String> list;

    public SessionsCellFactory(ObservableList<String> list) {
        this.list = list;
    }

    @Override
    protected void updateItem(String session, boolean empty) {
        super.updateItem(session, empty);
        if (session != null) {
            setText(String.format("%s", session));
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}
