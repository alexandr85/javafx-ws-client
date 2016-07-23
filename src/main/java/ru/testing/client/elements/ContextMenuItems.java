package ru.testing.client.elements;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.common.db.objects.Session;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;
import ru.testing.client.elements.message.DetailTab;
import ru.testing.client.elements.message.OutputMessage;
import ru.testing.client.elements.message.OutputMessageType;

import static ru.testing.client.elements.message.OutputMessageType.RECEIVED;
import static ru.testing.client.elements.message.OutputMessageType.SEND;

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
        MenuItem deleteAll = new MenuItem("Clear all");
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
        MenuItem deleteCell = new MenuItem("Clear cell");
        deleteCell.setOnAction(event -> cell.getListView().getItems().remove(cell.getIndex()));
        return deleteCell;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param cell ListCell<OutputMessage>
     * @return MenuItem
     */
    public MenuItem copyCellAll(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy cell data");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getText());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param cell ListCell<OutputMessage>
     * @return MenuItem
     */
    public MenuItem copyCellMessage(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy message");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getItem().getMessage());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem copyCellText(ListCell<String> cell) {
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
     * Menu item 'copy' for copy string to clipboard
     *
     * @param cell ListCell<OutputMessage>
     * @return MenuItem
     */
    public MenuItem copyCellTime(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy time");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.getItem().getFormattedTime());
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Menu item 'copy' for copy string to clipboard
     *
     * @param list ObservableList<OutputMessage>
     * @return MenuItem
     */
    public MenuItem copySelected(ObservableList<OutputMessage> list) {
        MenuItem copyItem = new MenuItem("Copy selected");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(getAllMessages(list));
            clipboard.setContent(content);
        });
        return copyItem;
    }

    /**
     * Save message from cell to file
     *
     * @param cell ListCell<OutputMessage>
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem saveMessageToFile(ListCell<OutputMessage> cell, MainController main) {
        MenuItem saveFileItem = new MenuItem("Save cell data");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(cell.getText(), main));
        return saveFileItem;
    }

    /**
     * Save all selected messages to file
     *
     * @param list ObservableList<OutputMessage>
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem saveSelectedToFile(ObservableList<OutputMessage> list, MainController main) {
        MenuItem saveFileItem = new MenuItem("Save selected");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(getAllMessages(list), main));
        return saveFileItem;
    }

    /**
     * Save send selected messages to file
     *
     * @param list ObservableList<OutputMessage>
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem saveSelectedSendToFile(ObservableList<OutputMessage> list, MainController main) {
        MenuItem saveFileItem = new MenuItem("Save only send");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(getMessagesByType(list, SEND), main));
        return saveFileItem;
    }

    /**
     * Save send selected messages to file
     *
     * @param list ObservableList<OutputMessage>
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem saveSelectedRecentToFile(ObservableList<OutputMessage> list, MainController main) {
        MenuItem saveFileItem = new MenuItem("Save only received");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(getMessagesByType(list, RECEIVED), main));
        return saveFileItem;
    }

    /**
     * Save send selected messages to file
     *
     * @param item OutputMessage
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem showMessage(OutputMessage item, MainController main) {
        MenuItem show = new MenuItem("Show message");
        show.setOnAction(event -> new DetailTab(item, main));
        return show;
    }

    /**
     * Delete selected session
     *
     * @return MenuItem
     */
    public MenuItem deleteSessionMenu(ListCell<Session> cell, SessionsController sessionsController) {
        MenuItem deleteMenu = new MenuItem("Delete");
        deleteMenu.setOnAction(event -> sessionsController.deleteSession(cell));
        return deleteMenu;
    }

    /**
     * Set session data from selected session
     *
     * @param main MainController
     * @return MenuItem
     */
    public MenuItem loadSession(ListCell<Session> cell, MainController main) {
        MenuItem loadSession = new MenuItem("Load");
        loadSession.setOnAction(event -> {
            main.setDataFromSession(cell.getItem().getId());
            main.getSessionsPopOver().hide();
        });
        return loadSession;
    }

    /**
     * Get all messages from list
     *
     * @param list ObservableList<OutputMessage>
     * @return String
     */
    private String getAllMessages(ObservableList<OutputMessage> list) {
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(m -> stringBuilder.append(String.format("%-8s %s %s\n",
                m.getMessageType(), m.getFormattedTime(), m.getMessage())));
        return stringBuilder.toString();
    }

    /**
     * Get filtered messages from list
     *
     * @param list ObservableList<OutputMessage>
     * @param type OutputMessageType
     * @return String
     */
    private String getMessagesByType(ObservableList<OutputMessage> list, OutputMessageType type) {
        StringBuilder stringBuilder = new StringBuilder();
        list.stream()
                .filter(m -> m.getMessageType() == type)
                .forEach(m -> stringBuilder.append(String.format("%s %s\n", m.getFormattedTime(), m.getMessage())));
        return stringBuilder.toString();
    }
}
