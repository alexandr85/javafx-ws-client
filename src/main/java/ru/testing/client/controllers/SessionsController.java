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
import ru.testing.client.common.FilesOperations;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.sessions.*;
import ru.testing.client.elements.sessions.session.FilterData;
import ru.testing.client.elements.sessions.session.SendHistoryData;
import ru.testing.client.elements.sessions.session.ConnectionData;
import ru.testing.client.elements.sessions.session.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Sessions controller
 */
public class SessionsController {

    private MainController mainController;
    private ObservableList<Session> sessionsList = FXCollections.observableArrayList();
    private FilesOperations filesOperations = new FilesOperations();

    /**
     * Labels
     */
    @FXML
    private Label noSessionsLabel;

    /**
     * List view
     */
    @FXML
    private ListView<Session> sessionsListView;

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
        sessionsListView.setCellFactory(listView -> new SessionsCellFactory(this, mainController));
        sessionsListView.getItems().addListener((ListChangeListener<Session>) change -> {
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

        readSessions();
    }

    /**
     * Add session to sessions list
     */
    @FXML
    private void addSession() {
        String sName = sessionName.getText();
        if (!sName.isEmpty()) {
            Sessions sessions = readSessions();
            List<Session> sessionsList = sessions.getSessions();
            if (sessionsList == null) {
                sessionsList = new ArrayList<>();
            }
            if (!existSessionName(sessionsList, sName)) {
                sessionsList.add(new Session(
                        sName,
                        new ConnectionData(mainController.getServerUrl(), mainController.getHeadersItems()),
                        new SendHistoryData(mainController.getSendMsgItems()),
                        new FilterData(mainController.getFilterStatus(), mainController.getFilterItems()))
                );
                sessions.setSessions(sessionsList);
                filesOperations.saveSessionsData(sessions);
                sessionName.clear();
                readSessions();
            } else {
                Dialogs.getWarningDialog("This session name is exist in list");
            }
        }
    }

    /**
     * Verify exist session name in list
     * @param sessionsList currenct List<Session>
     * @return boolean
     */
    private boolean existSessionName(List<Session> sessionsList, String newName) {
        for (Session session : sessionsList) {
            if (session.getName().equals(newName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Read sessions from file add collect sessions list
     * @return Sessions
     */
    public Sessions readSessions() {
        Sessions sessions = filesOperations.readSessionsData();
        if (sessions != null) {
            List<Session> sessionList = sessions.getSessions();
            sessionsList.clear();
            if (sessionList != null && sessionList.size() > 0) {
                sessions.getSessions().stream().forEach(session -> sessionsList.add(session));
            }
        } else {
            sessions = new Sessions();
        }
        return sessions;
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
