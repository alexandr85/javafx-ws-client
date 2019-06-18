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
import ru.testing.client.common.objects.SendMessage;
import ru.testing.client.websocket.SendMessagesCellFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Http headers controller
 */
public class SendMessagesController {

    private final ObservableList<String> list = FXCollections.observableArrayList();

    @FXML
    private TextField tfMsgValue;
    @FXML
    private Button btAddMsg;
    @FXML
    private Label lbCheckInfo;
    @FXML
    private Label noMessagesLabel;
    @FXML
    private CheckListView<String> checkListView;

    @FXML
    private void initialize() {

        // Setup messages list
        checkListView.setItems(list);
        checkListView.getItems().addListener((ListChangeListener<? super String>) change -> {
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
        checkTextFieldMessage();
        tfMsgValue.textProperty().addListener((observable, oldValue, newValue) -> checkTextFieldMessage());
        tfMsgValue.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addMessage();
            }
        });
    }

    public void setCheckListViewCellFactory(TabWsMessagesController controller) {
        checkListView.setCellFactory(list -> new SendMessagesCellFactory(this, controller));
    }

    /**
     * Add message to check views list
     */
    @FXML
    public void addMessage() {
        String text = tfMsgValue.getText().trim();
        if (!text.isEmpty()) {
            if (!list.contains(text)) {
                list.add(text);
            }
            tfMsgValue.clear();
            tfMsgValue.requestFocus();
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
            checkedList.add(i, new SendMessage(checkListView.getCheckModel().isChecked(i), list.get(i)));
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
     * Get check list views
     *
     * @return CheckListView<String>
     */
    public CheckListView<String> getCheckListView() {
        return checkListView;
    }

    /**
     * Show or hide headers list
     *
     * @param visible boolean visible status
     */
    private void setListViewVisible(boolean visible) {
        checkListView.setVisible(visible);
        checkListView.setManaged(visible);
        lbCheckInfo.setVisible(visible);
        lbCheckInfo.setManaged(visible);
        noMessagesLabel.setVisible(!visible);
        noMessagesLabel.setManaged(!visible);
    }

    /**
     * Check text field message value
     */
    private void checkTextFieldMessage() {
        String text = tfMsgValue.getText().trim();
        btAddMsg.setDisable(text.length() <= 0);
    }
}
