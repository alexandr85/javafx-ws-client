package ru.testing.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.CheckListView;
import ru.testing.client.common.db.objects.SendMessage;
import ru.testing.client.elements.message.SendMessagesCellFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Http headers controller
 */
public class SendMessagesController {

    private final ObservableList<String> list = FXCollections.observableArrayList();
    private MainController main;

    @FXML
    private TextField txMsgValue;
    @FXML
    private Button btAddMsg;
    @FXML
    private Label lbCheckInfo;
    @FXML
    private Label noMessagesLabel;
    @FXML
    private CheckListView<String> messagesList;

    public SendMessagesController(MainController mainController) {
        main = mainController;
    }

    @FXML
    private void initialize() {

        // Setup messages list
        messagesList.setItems(list);
        messagesList.setCellFactory(list -> new SendMessagesCellFactory(main, this));
        messagesList.getItems().addListener((ListChangeListener<? super String>) change -> {
            if (change.next()) {
                int size = list.size();
                if (size > 0) {
                    setListViewVisible(true);
                } else {
                    setListViewVisible(false);
                    noMessagesLabel.requestFocus();
                }
            }
        });

        // Setup new message value and add button
        txMsgValue.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = txMsgValue.getText().trim();
            btAddMsg.setDisable(text.length() <= 0);
        });
        txMsgValue.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addMessage();
            }
        });
    }

    /**
     * Add message to check view list
     */
    @FXML
    public void addMessage() {
        String text = txMsgValue.getText().trim();
        if (!text.isEmpty()) {
            if (!list.contains(text)) {
                list.add(text);
            }
            txMsgValue.clear();
            txMsgValue.requestFocus();
        }
    }

    /**
     * Get sent messages list with checked status
     *
     * @return List<SendMessage>
     */
    List<SendMessage> getSentMessages() {
        List<SendMessage> checkedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            checkedList.add(i, new SendMessage(messagesList.getCheckModel().isChecked(i), list.get(i)));
        }
        return checkedList;
    }

    /**
     * Get send message list
     *
     * @return ObservableList<String>
     */
    public ObservableList<String> getList() {
        return list;
    }

    /**
     * Get check list view
     *
     * @return CheckListView<String>
     */
    public CheckListView<String> getMessagesList() {
        return messagesList;
    }

    /**
     * Show or hide headers list
     *
     * @param visible boolean visible status
     */
    private void setListViewVisible(boolean visible) {
        messagesList.setVisible(visible);
        messagesList.setManaged(visible);
        lbCheckInfo.setVisible(visible);
        lbCheckInfo.setManaged(visible);
        noMessagesLabel.setVisible(!visible);
        noMessagesLabel.setManaged(!visible);
    }
}
