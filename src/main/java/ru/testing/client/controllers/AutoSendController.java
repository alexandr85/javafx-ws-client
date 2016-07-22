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
    private ObservableList<String> autoMsgList = FXCollections.observableArrayList();

    /**
     * Labels
     */
    @FXML
    private Label nonMessagesLabel;

    /**
     * List view
     */
    @FXML
    private ListView<String> autoMsgListView;

    /**
     * Text fields
     */
    @FXML
    private TextField messageField;

    public AutoSendController(MainController mainController) {
        main = mainController;
    }

    /**
     * Method run then this controller initialize
     */
    @FXML
    private void initialize() {
        autoMsgListView.setItems(autoMsgList);
        autoMsgListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        autoMsgListView.setCellFactory(listView -> new AutoSendCellFactory(main));
        autoMsgListView.getItems().addListener((ListChangeListener<String>) change -> {
            if (change.next()) {
                int size = autoMsgList.size();
                if (size > 0) {
                    setSessionsListVisible(true);
                } else {
                    setSessionsListVisible(false);
                    Platform.runLater(() -> nonMessagesLabel.requestFocus());
                }
            }
        });

        autoMsgListView.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ESCAPE) {
                main.getAutoSendPopOver().hide();
            }
        });

        messageField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addAutoMessage();
            }
            if (key.getCode() == KeyCode.ESCAPE) {
                main.getAutoSendPopOver().hide();
            }
        });

        Platform.runLater(() -> messageField.requestFocus());
    }

    /**
     * Get auto send message list
     * @return ObservableList<String>
     */
    public ObservableList<String> getAutoMsgList() {
        return autoMsgList;
    }

    /**
     * Add session to sessions list
     */
    @FXML
    private void addAutoMessage() {
        String message = this.messageField.getText();
        if (message != null && !message.isEmpty()) {
            autoMsgList.add(message);
            this.messageField.clear();
        }
    }

    /**
     * Show or hide sessions list
     *
     * @param visible boolean visible status
     */
    private void setSessionsListVisible(boolean visible) {
        autoMsgListView.setVisible(visible);
        autoMsgListView.setManaged(visible);
        nonMessagesLabel.setVisible(!visible);
        nonMessagesLabel.setManaged(!visible);
    }
}
