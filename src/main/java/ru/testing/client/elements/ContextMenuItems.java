package ru.testing.client.elements;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.controlsfx.control.IndexedCheckModel;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.elements.tabs.WsMessageTab;


/**
 * Class collected other menu items
 */
public class ContextMenuItems {

    /**
     * Menu item for clear all cell in list views
     *
     * @param list ObservableList
     * @return MenuItem
     */
    public MenuItem clearListView(ObservableList list) {
        MenuItem deleteAll = new MenuItem("Delete all");
        deleteAll.setOnAction(event -> list.clear());
        return deleteAll;
    }

    /**
     * Menu item for clear all cell in check list views
     *
     * @param list ObservableList
     * @return MenuItem
     */
    public MenuItem clearCheckListView(ObservableList list, IndexedCheckModel<String> checkModel) {
        MenuItem deleteAll = new MenuItem("Delete all");
        deleteAll.setOnAction(event -> {
            checkModel.clearChecks();
            list.clear();
        });
        return deleteAll;
    }

    /**
     * Menu item for delete selected item cell
     *
     * @param cell ListCell
     * @return MenuItem
     */
    public MenuItem removeCell(ListCell cell) {
        MenuItem deleteCell = new MenuItem("Delete item");
        deleteCell.setOnAction(event -> cell.getListView().getItems().remove(cell.getIndex()));
        return deleteCell;
    }

    /**
     * Menu item for deselect selected item cell
     *
     * @param list ListView<ReceivedMessage>
     * @return MenuItem
     */
    public MenuItem deselectCell(ListView<ReceivedMessage> list) {
        MenuItem deselect = new MenuItem("Deselect item");
        deselect.setOnAction(event -> list.getSelectionModel().clearSelection());
        return deselect;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public MenuItem copyCellAll(ReceivedMessage item) {
        MenuItem copyItem = new MenuItem("Copy full message");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(item.toString());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy item message string to clipboard
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public MenuItem copyCellMessage(ReceivedMessage item) {
        MenuItem copyItem = new MenuItem("Copy body message");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(item.getMessage());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public MenuItem copyCellTime(ReceivedMessage item) {
        MenuItem copyItem = new MenuItem("Copy time message");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(item.getFormattedTime());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Save message from cell to file
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public MenuItem saveMessageToFile(ReceivedMessage item) {
        MenuItem saveFileItem = new MenuItem("Save full message");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(item.toString()));
        return saveFileItem;
    }

    /**
     * Save send selected messages to file
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public MenuItem showMessage(final ReceivedMessage item) {
        MenuItem show = new MenuItem("Show body message");
        show.setOnAction(event -> new WsMessageTab(item));
        return show;
    }

    /**
     * Menu item 'copy' for copy cell string to clipboard
     *
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem copySendMsg(ListCell<String> cell) {
        MenuItem copyItem = new MenuItem("Copy message");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getText());
            clipboard.setContent(content);
        });
        return copyItem;
    }
}
