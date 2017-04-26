package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.http.settings.HeadersCellFactory;
import ru.testing.client.elements.http.settings.HttpParametersCellFactory;

/**
 * Http headers controller
 */
public class HttpSettingsController {

    private final ObservableList<Header> headerObservableList = FXCollections.observableArrayList();
    private final ObservableList<HttpParameter> httpParameterObservableList = FXCollections.observableArrayList();

    @FXML
    private TitledPane headersPane;
    @FXML
    private TitledPane parametersPane;
    @FXML
    private TextField headerName;
    @FXML
    private TextField headerValue;
    @FXML
    private TextField parameterName;
    @FXML
    private TextField parameterValue;
    @FXML
    private ListView<Header> headerListView;
    @FXML
    private ListView<HttpParameter> parametersListView;
    @FXML
    private Label lbNoHeaders;
    @FXML
    private Label lbNoParameters;

    @FXML
    private void initialize() {

        // Prepare headers list
        headerListView.setItems(headerObservableList);
        headerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        headerListView.setCellFactory(listView -> new HeadersCellFactory(headerObservableList));
        headerListView.getItems().addListener((ListChangeListener<Header>) change -> {
            if (change.next()) {
                int size = headerObservableList.size();
                if (size > 0) {
                    setListViewVisible(headerListView, lbNoHeaders, true);
                } else {
                    setListViewVisible(headerListView, lbNoHeaders, false);
                    lbNoHeaders.requestFocus();
                }
            }
        });

        // Prepare parameters list
        parametersListView.setItems(httpParameterObservableList);
        parametersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        parametersListView.setCellFactory(listView -> new HttpParametersCellFactory(httpParameterObservableList));
        parametersListView.getItems().addListener((ListChangeListener<HttpParameter>) change -> {
            if (change.next()) {
                int size = httpParameterObservableList.size();
                if (size > 0) {
                    setListViewVisible(parametersListView, lbNoParameters, true);
                } else {
                    setListViewVisible(parametersListView, lbNoParameters, false);
                    lbNoParameters.requestFocus();
                }
            }
        });

        // Actions on headers text fields
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

        // Actions on parameters text fields
        parameterName.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addParameter();
            }
        });
        parameterValue.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                addParameter();
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
     * Add header data to headers list
     */
    @FXML
    private void addParameter() {
        final String name = parameterName.getText();
        final String value = parameterValue.getText();
        if (!name.isEmpty()) {
            if (!value.isEmpty()) {
                Platform.runLater(() -> {
                    httpParameterObservableList.add(new HttpParameter(name, value));
                    parameterName.clear();
                    parameterValue.clear();
                });
                parameterName.requestFocus();
            } else {
                parameterName.requestFocus();
            }
        } else {
            parameterName.requestFocus();
        }
    }

    /**
     * Get headers list
     *
     * @return ObservableList<Header>
     */
    ObservableList<Header> getHeadersList() {
        return headerObservableList;
    }

    /**
     * Get http parameters list
     *
     * @return ObservableList<HttpParameter>
     */
    ObservableList<HttpParameter> getHttpParametersList() {
        return httpParameterObservableList;
    }

    /**
     * Show or hide list view
     *
     * @param listView ListView
     * @param visible  boolean visible status
     */
    private void setListViewVisible(ListView listView, Label label, boolean visible) {
        listView.setVisible(visible);
        listView.setManaged(visible);
        label.setVisible(!visible);
        label.setManaged(!visible);
    }

    /**
     * Get header list view
     *
     * @return ListView<Header>
     */
    public ListView<Header> getHeadersListView() {
        return headerListView;
    }

    /**
     * Get http parameters list view
     *
     * @return ListView<HttpParameter>
     */
    public ListView<HttpParameter> getParametersListView() {
        return parametersListView;
    }
}
