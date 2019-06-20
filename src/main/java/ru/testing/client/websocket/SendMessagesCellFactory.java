package ru.testing.client.websocket;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import org.controlsfx.control.IndexedCheckModel;
import org.controlsfx.control.PopOver;
import ru.testing.client.controllers.SendMessagesController;
import ru.testing.client.controllers.TabWsMessagesController;
import ru.testing.client.elements.ContextMenuItems;

/**
 * History send message cell factory
 */
public class SendMessagesCellFactory extends CheckBoxListCell<String> {

    private ObservableList<String> list;
    private TextField sendMsgTextField;
    private PopOver sentMsgPopOver;
    private IndexedCheckModel<String> checkModel;

    public SendMessagesCellFactory(SendMessagesController sendMessagesController,
                                   TabWsMessagesController tabWsMessagesController) {
        list = sendMessagesController.getList();
        sendMsgTextField = tabWsMessagesController.getSendMsgTextField();
        sentMsgPopOver = tabWsMessagesController.getSendMessagesPopOver();
        checkModel = sendMessagesController.getCheckListView().getCheckModel();
    }

    @Override
    public void updateItem(String message, boolean empty) {

        super.setSelectedStateCallback(param -> {
            var observable = new SimpleBooleanProperty();
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
                    var cellText = getText();
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
        var contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                ContextMenuItems.copySendMsg(cell),
                ContextMenuItems.clearCheckListView(list, checkModel)
        );
        return contextMenu;
    }
}
