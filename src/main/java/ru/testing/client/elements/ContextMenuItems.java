package ru.testing.client.elements;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import ru.testing.client.common.FilesOperations;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;
import ru.testing.client.elements.message.OutputMessage;
import ru.testing.client.elements.sessions.Session;
import ru.testing.client.elements.sessions.Sessions;

import java.util.Iterator;

/**
 * Class collected other menu items
 */
public class ContextMenuItems {

    /**
     * Menu item for clear all cell in list view
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
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem getCopyCellAll(ListCell<OutputMessage> cell) {
        MenuItem copyItem = new MenuItem("Copy cell");
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
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem getCopyCellMessage(ListCell<OutputMessage> cell) {
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
     * @param cell ListCell<String>
     * @return MenuItem
     */
    public MenuItem getCopyCellTime(ListCell<OutputMessage> cell) {
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
     * Save message from cell to file
     * @param cell ListCell<OutputMessage>
     * @return MenuItem
     */
    public MenuItem saveMessageToFile(ListCell<OutputMessage> cell) {
        MenuItem saveFileItem = new MenuItem("Save cell to file");
        saveFileItem.setOnAction(event -> new FilesOperations().saveTextToFile(cell.getText()));
        return saveFileItem;
    }

    /**
     * Delete selected session
     * @param cell ListCell<Session>
     * @return MenuItem
     */
    public MenuItem deleteSession(ListCell<Session> cell, SessionsController sessionsController) {
        MenuItem deleteSession = new MenuItem("Delete");
        deleteSession.setOnAction(event -> {
            FilesOperations filesOperations = new FilesOperations();
            Sessions sessions = filesOperations.readSessionsData();
            Iterator<Session> iterator = sessions.getSessions().iterator();
            while (iterator.hasNext()) {
                Session session = iterator.next();
                if (session.getName().equals(cell.getItem().getName())) {
                    iterator.remove();
                }
            }
            filesOperations.saveSessionsData(sessions);
            sessionsController.readSessionsFromFile();
        });
        return deleteSession;
    }

    /**
     * Set session data from selected session
     * @param cell ListCell<Session> cell
     * @param mainController MainController
     * @return MenuItem
     */
    public MenuItem loadSession(ListCell<Session> cell, MainController mainController) {
        MenuItem loadSession = new MenuItem("Load");
        loadSession.setOnAction(event -> {
            mainController.setDataFromSession(cell.getItem());
            mainController.getSessionsPopOver().hide();
        });
        return loadSession;
    }
}
