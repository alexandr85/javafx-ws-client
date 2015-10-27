package ru.testing.client.gui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ru.testing.client.message.OutputMessage;

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
}
