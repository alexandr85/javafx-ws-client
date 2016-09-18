package ru.testing.client.elements.message;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import org.controlsfx.control.IndexedCheckModel;
import org.controlsfx.control.PopOver;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SendMessagesController;
import ru.testing.client.elements.ContextMenuItems;

/**
 * History send message cell factory
 */
public class SendMessagesCellFactory extends CheckBoxListCell<String> {

    private ObservableList<String> list;
    private TextField sendMsgTextField;
    private PopOver sentMsgPopOver;
    private IndexedCheckModel<String> checkModel;

    public SendMessagesCellFactory(MainController mainController, SendMessagesController sendMessagesController) {
        this.list = sendMessagesController.getList();
        sendMsgTextField = mainController.getSendMsgTextField();
        sentMsgPopOver = mainController.getSendMessagesPopOver();
        checkModel = sendMessagesController.getCheckListView().getCheckModel();
    }

    @Override
    public void updateItem(String message, boolean empty) {

        super.setSelectedStateCallback(param -> {
            BooleanProperty observable = new SimpleBooleanProperty();
            observable.addListener((obs, oldValue, newValue) -> {
                if (newValue) {
                    checkModel.check(param);
                } else {
                    checkModel.clearCheck(param);
                }
            });
            observable.setValue(checkModel.isChecked(param));
            return observable;
        });

        super.updateItem(message, empty);
        if (message != null) {
            setText(message);
            setContextMenu(getSendHistoryContextMenu(this, checkModel));
            setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    String cellText = getText();
                    if (cellText != null && !cellText.isEmpty() && !sendMsgTextField.isDisable()) {
                        sendMsgTextField.setText(getText());
                        sentMsgPopOver.hide();
                    }
                }
            });
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    /**
     * Context menu for history pop over
     *
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getSendHistoryContextMenu(ListCell<String> cell, IndexedCheckModel<String> checkModel) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.copySendMsg(cell),
                m.clearCheckListView(list, checkModel)
        );
        return contextMenu;
    }
}
