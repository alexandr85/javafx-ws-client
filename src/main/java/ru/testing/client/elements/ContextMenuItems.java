package ru.testing.client.elements;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.controlsfx.control.IndexedCheckModel;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.elements.tabs.WsMessageTab;

import java.util.*;


/**
 * Class collected other menu items
 */
public abstract class ContextMenuItems {

    private static final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();
    private static final ClipboardContent CLIPBOARD_CONTENT = new ClipboardContent();

    private ContextMenuItems() {
    }

    /**
     * Menu item for clear all cell in list views
     *
     * @param list ObservableList
     * @return MenuItem
     */
    public static MenuItem clearListView(ObservableList list) {
        var menu = new MenuItem("Delete all");
        menu.setOnAction(event -> list.clear());
        return menu;
    }

    /**
     * Menu item for clear all cell in check list views
     *
     * @param list ObservableList
     * @return MenuItem
     */
    public static MenuItem clearCheckListView(ObservableList list, IndexedCheckModel<String> checkModel) {
        var menu = new MenuItem("Delete all");
        menu.setOnAction(event -> {
            checkModel.clearChecks();
            list.clear();
        });
        return menu;
    }

    /**
     * Menu item for delete selected item cell
     *
     * @param cell ListCell
     * @return MenuItem
     */
    public static MenuItem removeCell(ListCell cell) {
        var menu = new MenuItem("Delete item");
        menu.setOnAction(event -> cell.getListView().getItems().remove(cell.getIndex()));
        return menu;
    }

    /**
     * Menu item for deselect selected item cell
     *
     * @param list ListView<ReceivedMessage>
     * @return MenuItem
     */
    public static MenuItem deselectCell(ListView<ReceivedMessage> list) {
        var menu = new MenuItem("Deselect item");
        menu.setOnAction(event -> list.getSelectionModel().clearSelection());
        return menu;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public static MenuItem copyCellAll(ReceivedMessage item) {
        var menu = new MenuItem("Copy full message");
        menu.setOnAction(event -> {
            CLIPBOARD_CONTENT.putString(item.toString());
            CLIPBOARD.setContent(CLIPBOARD_CONTENT);
        });
        return menu;
    }

    /**
     * Menu item 'copy' for copy item message string to clipboard
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public static MenuItem copyCellMessage(ReceivedMessage item) {
        var menu = new MenuItem("Copy body message");
        menu.setOnAction(event -> {
            CLIPBOARD_CONTENT.putString(item.getMessage());
            CLIPBOARD.setContent(CLIPBOARD_CONTENT);
        });
        return menu;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public static MenuItem copyCellTime(ReceivedMessage item) {
        var menu = new MenuItem("Copy time message");
        menu.setOnAction(event -> {
            CLIPBOARD_CONTENT.putString(item.getFormattedTime());
            CLIPBOARD.setContent(CLIPBOARD_CONTENT);
        });
        return menu;
    }

    /**
     * Save message from cell to file
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public static MenuItem saveMessageToFile(ReceivedMessage item) {
        var menu = new MenuItem("Save full message");
        menu.setOnAction(event -> new FilesOperations().saveTextToFile(item.toString()));
        return menu;
    }

    /**
     * Save send selected messages to file
     *
     * @param item ReceivedMessage
     * @return MenuItem
     */
    public static MenuItem showMessage(final ReceivedMessage item) {
        var menu = new MenuItem("Show body message");
        menu.setOnAction(event -> new WsMessageTab(item));
        return menu;
    }

    /**
     * Menu item for copy cell string to clipboard
     *
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public static MenuItem copySendMsg(ListCell<String> cell) {
        var menu = new MenuItem("Copy message");
        menu.setOnAction(event -> {
            CLIPBOARD_CONTENT.putString(cell.getText());
            CLIPBOARD.setContent(CLIPBOARD_CONTENT);
        });
        return menu;
    }

    /**
     * Menu item for copy cell string from json tree view
     *
     * @param tv json tree view
     * @return MenuItem
     */
    static MenuItem copyTreeCellValue(TreeView<String> tv) {
        var menu = new MenuItem("Copy key:value");
        menu.setOnAction(event -> {
            Optional<TreeItem<String>> find = tv.getSelectionModel().getSelectedItems().stream().findFirst();
            if (find.isPresent()) {
                CLIPBOARD_CONTENT.putString(find.get().getValue());
                CLIPBOARD.setContent(CLIPBOARD_CONTENT);
            }
        });
        return menu;
    }

    /**
     * Menu item for copy tree cells strings from json tree view
     *
     * @param tv json tree view
     * @return MenuItem
     */
    static MenuItem copyTreeValues(TreeView<String> tv) {
        var menu = new MenuItem("Copy tree keys:values");
        menu.setOnAction(event -> {
            Optional<TreeItem<String>> find = tv.getSelectionModel().getSelectedItems().stream().findFirst();
            if (find.isPresent()) {
                TreeItem<String> item = find.get();
                var buffer = new StringBuffer();
                getChildrenValues(buffer, item);
                CLIPBOARD_CONTENT.putString(buffer.toString());
                CLIPBOARD.setContent(CLIPBOARD_CONTENT);
            }
        });
        return menu;
    }

    private static void getChildrenValues(StringBuffer buffer, TreeItem<String> item) {
        buffer.append(String.format("%s\n", item.getValue()));
        item.getChildren().forEach( i -> {
            if (i.getChildren().size() > 0) {
                i.getChildren().forEach( c -> getChildrenValues(buffer, c));
            } else {
                buffer.append(String.format("%s\n", i.getValue()));
            }
        });
    }
}
