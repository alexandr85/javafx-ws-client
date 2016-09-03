package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ru.testing.client.common.db.objects.Header;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.headers.HeadersCellFactory;

import java.util.List;

/**
 * Http settings controller
 */
public class HeadersController {

    private MainController main;
    private ObservableList<Header> headerObservableList = FXCollections.observableArrayList();

    public HeadersController(MainController main) {
        this.main = main;
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
        headerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        headerList.setCellFactory(listView -> new HeadersCellFactory(headerObservableList));
        headerList.getItems().addListener((ListChangeListener<Header>) change -> {
            if (change.next()) {
                int size = headerObservableList.size();
                main.getHeadersCount().setText(String.valueOf(size));
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
                    headerName.requestFocus();
                } else {
                    new Dialogs().getWarningDialog("Headers max size is 9");
                }
            } else {
                headerValue.requestFocus();
            }
        } else {
            headerName.requestFocus();
        }
    }

    /**
     * Get headers list
     *
     * @return ObservableList<Header>
     */
    public ObservableList<Header> getHeaderObservableList() {
        return headerObservableList;
    }

    /**
     * Set headers data
     *
     * @param headers List<Header>
     */
    public void setHeaders(List<Header> headers) {
        Platform.runLater(() -> {
            headerObservableList.clear();
            headers.stream().forEach(header -> headerObservableList.add(new Header(header.getName(), header.getValue())));
            //main.getHeadersCount().setText(String.valueOf(headers.size()));
        });
    }

    /**
     * Show or hide headers list
     *
     * @param visible boolean visible status
     */
    private void setHeaderVisible(boolean visible) {
        headerList.setVisible(visible);
        headerList.setManaged(visible);
        noHeadersLabel.setVisible(!visible);
        noHeadersLabel.setManaged(!visible);
    }
}
