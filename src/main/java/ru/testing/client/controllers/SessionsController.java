package ru.testing.client.controllers;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ru.testing.client.elements.sessions.SessionsCellFactory;

/**
 * Sessions controller
 */
public class SessionsController {

    private MainController mainController;
    private ObservableList<String> sessionsList = FXCollections.observableArrayList();

    /**
     * Labels
     */
    @FXML
    private Label noSessionsLabel;

    /**
     * List view
     */
    @FXML
    private ListView<String> sessionsListView;

    /**
     * Text fields
     */
    @FXML
    private TextField sessionName;

    public SessionsController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {
        Platform.runLater(() -> noSessionsLabel.requestFocus());
        sessionsListView.setItems(sessionsList);
        sessionsListView.setCellFactory(listView -> new SessionsCellFactory(sessionsList));
        sessionsListView.getItems().addListener((ListChangeListener<String>) change -> {
            if (change.next()) {
                int size = sessionsList.size();
                if (size > 0) {
                    setSessionsListVisible(true);
                } else {
                    setSessionsListVisible(false);
                    Platform.runLater(() -> noSessionsLabel.requestFocus());
                }
            }
        });

        sessionName.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addSession();
            }
        });
    }

    /**
     * Add session to sessions list
     */
    @FXML
    private void addSession() {
        if (sessionName != null && !sessionName.getText().isEmpty()) {
            sessionsList.add(sessionName.getText());
            sessionName.clear();
        }
    }

    /**
     * Show or hide sessions list
     * @param visible boolean visible status
     */
    private void setSessionsListVisible(boolean visible) {
        sessionsListView.setVisible(visible);
        sessionsListView.setManaged(visible);
        noSessionsLabel.setVisible(!visible);
        noSessionsLabel.setManaged(!visible);
    }
}
