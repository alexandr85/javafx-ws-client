package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ru.testing.client.elements.autosend.AutoSendCellFactory;

/**
 * Sessions controller
 */
public class AutoSendController {

    private MainController main;
    private ObservableList<String> autoSendMessagesList = FXCollections.observableArrayList();

    /**
     * Labels
     */
    @FXML
    private Label noAutoMessageLabel;

    /**
     * List view
     */
    @FXML
    private ListView<String> autoSendMessagesListView;

    /**
     * Text fields
     */
    @FXML
    private TextField autoSendMessage;

    public AutoSendController(MainController mainController) {
        main = mainController;
    }

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {
        Platform.runLater(() -> noAutoMessageLabel.requestFocus());
        autoSendMessagesListView.setItems(autoSendMessagesList);
        autoSendMessagesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        autoSendMessagesListView.setCellFactory(listView -> new AutoSendCellFactory(main));
        autoSendMessagesListView.getItems().addListener((ListChangeListener<String>) change -> {
            if (change.next()) {
                int size = autoSendMessagesList.size();
                if (size > 0) {
                    setSessionsListVisible(true);
                } else {
                    setSessionsListVisible(false);
                    Platform.runLater(() -> noAutoMessageLabel.requestFocus());
                }
            }
        });

        autoSendMessage.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addAutoMessage();
            }
        });
    }

    /**
     * Add session to sessions list
     */
    @FXML
    private void addAutoMessage() {
        String message = autoSendMessage.getText();
        if (message != null && !message.isEmpty()) {
            autoSendMessagesList.add(message);
            autoSendMessage.clear();
        }
    }

    /**
     * Show or hide sessions list
     *
     * @param visible boolean visible status
     */
    private void setSessionsListVisible(boolean visible) {
        autoSendMessagesListView.setVisible(visible);
        autoSendMessagesListView.setManaged(visible);
        noAutoMessageLabel.setVisible(!visible);
        noAutoMessageLabel.setManaged(!visible);
    }
}
