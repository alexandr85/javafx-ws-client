package ru.testing.client.common.message;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import org.controlsfx.control.PopOver;
import ru.testing.client.tools.ContextMenuItems;

/**
 * History send message cell factory
 */
public class SendHistoryCellFactory extends ListCell<String> {

    private ObservableList<String> list;
    private TextField sendMsgTextField;
    private PopOver historyPopOver;

    public SendHistoryCellFactory(ObservableList<String> list, TextField sendMsgTextField, PopOver historyPopOver) {
        this.list = list;
        this.sendMsgTextField = sendMsgTextField;
        this.historyPopOver = historyPopOver;
    }

    @Override
    protected void updateItem(String message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null) {
            setText(message);
            setContextMenu(getSendHistoryContextMenu(this));
            setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    String cellText = getText();
                    if (cellText != null && !cellText.isEmpty() && !sendMsgTextField.isDisable()) {
                        sendMsgTextField.setText(getText());
                        historyPopOver.hide();
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
     * @param cell ListCell string
     * @return ContextMenu
     */
    private ContextMenu getSendHistoryContextMenu(ListCell<String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        ContextMenuItems m = new ContextMenuItems();
        contextMenu.getItems().addAll(
                m.clearPopOverCell(cell),
                m.clearListView(list)
        );
        return contextMenu;
    }
}
