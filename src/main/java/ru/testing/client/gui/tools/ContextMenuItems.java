package ru.testing.client.gui.tools;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.controlsfx.control.PopOver;
import ru.testing.client.gui.message.OutputMessage;

/**
 * Class collected other menu items
 */
public class ContextMenuItems {

    /**
     * Menu item for clear all cell in list view
     * @param listView ListView
     * @return MenuItem
     */
    public MenuItem clearListView(ListView listView) {
        MenuItem deleteAll = new MenuItem("Clear all");
        deleteAll.setOnAction((event -> listView.getItems().clear()));
        return deleteAll;
    }

    /**
     * Clear history send message pop over list
     * @param list ObservableList send message list
     * @param popOver PopOver
     * @param button ToggleButton
     * @return MenuItem
     */
    public MenuItem clearHistoryList(ObservableList list, PopOver popOver, ToggleButton button) {
        MenuItem deleteAll = new MenuItem("Clear all");
        deleteAll.setOnAction((event -> {
            list.clear();
            popOver.hide();
            button.setDisable(true);
        }));
        return deleteAll;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem getCopyCellAll(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy cell");
        copyItem.setOnAction((event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getText());
            clipboard.setContent(content);
        }));
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem getCopyCellMessage(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy message");
        copyItem.setOnAction((event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getItem().getMessage());
            clipboard.setContent(content);
        }));
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem getCopyCellTime(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy time");
        copyItem.setOnAction((event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getItem().getFormattedTime());
            clipboard.setContent(content);
        }));
        return copyItem;
    }

    /**
     * Save message from cell to file
     * @param cell ListCell<OutputMessage>
     * @return MenuItem
     */
    public MenuItem saveMessageToFile(ListCell<OutputMessage> cell) {
        MenuItem saveFileItem = new MenuItem("Save cell to file");
        saveFileItem.setOnAction((event -> new FilesOperations().saveTextToFile(cell.getText())));
        return saveFileItem;
    }
}
