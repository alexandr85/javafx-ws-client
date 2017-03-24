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

import static ru.testing.client.MainApp.getMainController;

/**
 * Http headers controller
 */
public class HeadersController {

    private final ObservableList<Header> headerObservableList = FXCollections.observableArrayList();

    @FXML
    private TextField headerName;
    @FXML
    private TextField headerValue;
    @FXML
    private ListView<Header> headerListView;
    @FXML
    private Label lbNoHeaders;

    @FXML
    private void initialize() {
        Platform.runLater(() -> lbNoHeaders.requestFocus());
        headerListView.setItems(headerObservableList);
        headerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        headerListView.setCellFactory(listView -> new HeadersCellFactory(headerObservableList));
        headerListView.getItems().addListener((ListChangeListener<Header>) change -> {
            if (change.next()) {
                int size = headerObservableList.size();
                getMainController().getLbHeadersCounter().setText(String.valueOf(size));
                if (size > 0) {
                    setListViewVisible(true);
                } else {
                    setListViewVisible(false);
                    lbNoHeaders.requestFocus();
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
        final String name = headerName.getText();
        final String value = headerValue.getText();
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
    ObservableList<Header> getHeaderObservableList() {
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
            headers.forEach(header -> headerObservableList.add(new Header(header.getName(), header.getValue())));
            getMainController().getLbHeadersCounter().setText(String.valueOf(headers.size()));
        });
    }

    /**
     * Show or hide headers list
     *
     * @param visible boolean visible status
     */
    private void setListViewVisible(boolean visible) {
        headerListView.setVisible(visible);
        headerListView.setManaged(visible);
        lbNoHeaders.setVisible(!visible);
        lbNoHeaders.setManaged(!visible);
    }

    /**
     * Get header list view
     *
     * @return ListView<Header>
     */
    public ListView<Header> getHeaderListView() {
        return headerListView;
    }
}
