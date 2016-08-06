package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ru.testing.client.common.db.objects.Session;
import ru.testing.client.elements.Dialogs;

import java.util.List;

import static ru.testing.client.common.db.Data.getData;

/**
 * Sessions controller
 */
public class ProfilesController {

    private MainController main;
    private ObservableList<Session> sessionsList = FXCollections.observableArrayList();

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
    private TextField sessionNameField;

    public ProfilesController(MainController mainController) {
        main = mainController;
    }

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {
        sessionsListView.setItems(sessionsList);
        sessionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //sessionsListView.setCellFactory(listView -> new SessionsCellFactory(this, main));
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

        sessionsListView.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ESCAPE) {
                //main.getSessionsPopOver().hide();
            }
        });

        sessionNameField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addSession();
            }
            if (key.getCode() == KeyCode.ESCAPE) {
                //main.getSessionsPopOver().hide();
            }
        });

        sessionNameField.requestFocus();

        getData().getSessions().forEach(session -> sessionsList.add(session));
    }

    /**
     * Add session to sessions list
     */
    @FXML
    private void addSession() {
        String sName = sessionNameField.getText();
        if (!sName.isEmpty()) {
            main.setProgressVisible(true);
            if (!existSessionName(sessionsList, sName)) {
                Session session = new Session(sName,
                        main.getServerUrl(),
                        main.isFiltered(),
                        main.isFilterVisible(),
                        main.isAutoScroll(),
                        main.isStatusBarShow());
                int id = getData().setSession(session);
                getData().setFilters(main.getFilterList(), id);
                //getData().setAutoMessages(main.getAutoSendPopOver().getController().getAutoMsgList(), id);
                getData().setHeaders(main.getHeadersList(), id);
                getData().setTxMessages(main.getSendMsgItems(), id);
                getData().setRxMessages(main.getOutputMessageList(), id);
                session.setId(id);
                sessionsList.add(session);
                sessionNameField.clear();
            } else {
                Dialogs.getWarningDialog("This session name is exist in list");
            }
            main.setProgressVisible(false);
        }
    }

    /**
     * Delete session from sessions list
     *
     * @param cell ListCell<Session>
     */
    public void deleteSession(ListCell<Session> cell) {
        Session session = cell.getItem();
        getData().deleteSession(session.getId());
        for (Session s : sessionsList) {
            if (s.getId() == session.getId()) {
                sessionsList.remove(cell.getIndex());
                return;
            }
        }
    }

    /**
     * Verify exist session name in list
     *
     * @param sessionsList current List<Session>
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
     * Show or hide sessions list
     *
     * @param visible boolean visible status
     */
    private void setSessionsListVisible(boolean visible) {
        sessionsListView.setVisible(visible);
        sessionsListView.setManaged(visible);
        noSessionsLabel.setVisible(!visible);
        noSessionsLabel.setManaged(!visible);
    }
}
