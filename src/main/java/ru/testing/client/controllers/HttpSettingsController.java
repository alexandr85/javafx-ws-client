package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.headers.Header;
import ru.testing.client.elements.headers.HeadersCellFactory;

/**
 * Http settings controller
 */
public class HttpSettingsController {

    private MainController mainController;
    private ObservableList<Header> headerObservableList = FXCollections.observableArrayList();

    public HttpSettingsController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Text fields
     */
    @FXML
    private TextField headerName;
    @FXML
    private TextField headerValue;

    /**
     * List view
     */
    @FXML
    private ListView<Header> headerList;

    /**
     * Label
     */
    @FXML
    private Label noHeadersLabel;

    @FXML
    private void initialize() {
        Platform.runLater(() -> noHeadersLabel.requestFocus());
        headerList.setItems(headerObservableList);
        headerList.setCellFactory(listView -> new HeadersCellFactory(headerObservableList));
        headerList.getItems().addListener((ListChangeListener<Header>) change -> {
            if (change.next()) {
                int size = headerObservableList.size();
                mainController.setHeaderCount(size);
                if (size > 0) {
                    setHeaderVisible(true);
                } else {
                    setHeaderVisible(false);
                    Platform.runLater(() -> noHeadersLabel.requestFocus());
                }
            }
        });
        headerName.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addHeader();
            }
        });
        headerValue.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addHeader();
            }
        });
    }

    /**
     * Add header data to headers list
     */
    @FXML
    private void addHeader() {
        String name = headerName.getText();
        String value = headerValue.getText();
        if (!name.isEmpty()) {
            if (!value.isEmpty()) {
                if (headerObservableList.size() < 9) {
                    Platform.runLater(() -> {
                        headerObservableList.add(new Header(name, value));
                        headerName.clear();
                        headerValue.clear();
                    });
                    mainController.setHeaders(headerList.getItems());
                    headerName.requestFocus();
                } else {
                    Dialogs.getWarningDialog("Headers max size is 9");
                }
            } else {
                headerValue.requestFocus();
            }
        } else {
            headerName.requestFocus();
        }
    }

    /**
     * Show or hide headers list
     * @param visible boolean visible status
     */
    private void setHeaderVisible(boolean visible) {
        headerList.setVisible(visible);
        headerList.setManaged(visible);
        noHeadersLabel.setVisible(!visible);
        noHeadersLabel.setManaged(!visible);
    }
}
