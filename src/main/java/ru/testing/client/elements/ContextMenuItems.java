package ru.testing.client.elements;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.controllers.MainController;
import ru.testing.client.elements.message.DetailTab;
import ru.testing.client.elements.message.OutputMessage;


/**
 * Class collected other menu items
 */
public class ContextMenuItems {

    /**
     * Menu item for clear all cell in list view
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
     * Menu item for delete selected item cell
     *
     * @param cell ListCell
     * @return MenuItem
     */
    public MenuItem clearPopOverCell(ListCell cell) {
        MenuItem deleteCell = new MenuItem("Delete item");
        deleteCell.setOnAction(event -> cell.getListView().getItems().remove(cell.getIndex()));
        return deleteCell;
    }

    /**
     * Menu item for deselect selected item cell
     *
     * @param list ListView<OutputMessage>
     * @return MenuItem
     */
    public MenuItem deselectCell(ListView<OutputMessage> list) {
        MenuItem deselect = new MenuItem("Deselect item");
        deselect.setOnAction(event -> list.getSelectionModel().clearSelection());
        return deselect;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param item OutputMessage
     * @return MenuItem
     */
    public MenuItem copyCellAll(OutputMessage item) {
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
     * @param item OutputMessage
     * @return MenuItem
     */
    public MenuItem copyCellMessage(OutputMessage item) {
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
     * @param item OutputMessage
     * @return MenuItem
     */
    public MenuItem copyCellTime(OutputMessage item) {
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
     * @param item OutputMessage
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem saveMessageToFile(OutputMessage item, MainController main) {
        MenuItem saveFileItem = new MenuItem("Save full message");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(item.toString(), main));
        return saveFileItem;
    }

    /**
     * Save send selected messages to file
     *
     * @param item OutputMessage
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem showMessage(final OutputMessage item, MainController main) {
        MenuItem show = new MenuItem("Show body message");
        show.setOnAction(event -> new DetailTab(item, main));
        return show;
    }

    /**
     * Menu item 'copy' for copy cell string to clipboard
     *
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem copyHistoryText(ListCell<String> cell) {
        MenuItem copyItem = new MenuItem("Copy message");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getText());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Set session data from selected session
     *
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem loadSession(ListCell<Profile> cell, MainController main) {
        MenuItem loadSession = new MenuItem("Load session");
        loadSession.setOnAction(event -> {
            main.setDataFromSession(cell.getItem().getId());
            //main.getSessionsPopOver().hide();
        });
        return loadSession;
    }
}
