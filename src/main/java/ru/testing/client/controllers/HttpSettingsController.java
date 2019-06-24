package ru.testing.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import ru.testing.client.MainApp;
import ru.testing.client.common.HttpTypes;
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
    private Accordion accordion;
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
    private ListView<Header> hListView;
    @FXML
    private ListView<HttpParameter> pListView;
    @FXML
    private FlowPane fpParameters;
    @FXML
    private TilePane tpParameters;
    @FXML
    private TextArea bodyTextArea;
    @FXML
    private Label lbNoHeaders;
    @FXML
    private Label lbNoParameters;

    @FXML
    private void initialize() {

        if (MainApp.getMainController().getHttpType() == HttpTypes.WEBSOCKET) {
            accordion.setExpandedPane(headersPane);
            headersPane.setCollapsible(false);
            parametersPane.setVisible(false);
            parametersPane.setManaged(false);
        }

        // Prepare headers list
        hListView.setItems(headerObservableList);
        hListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        hListView.setCellFactory(listView -> new HeadersCellFactory(headerObservableList));
        hListView.getItems().addListener((ListChangeListener<Header>) change -> {
            if (change.next()) {
                var size = headerObservableList.size();
                if (size > 0) {
                    setListViewVisible(hListView, lbNoHeaders, true);
                } else {
                    setListViewVisible(hListView, lbNoHeaders, false);
                    lbNoHeaders.requestFocus();
                }
            }
        });

        // Prepare parameters list
        pListView.setItems(httpParameterObservableList);
        pListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pListView.setCellFactory(listView -> new HttpParametersCellFactory(httpParameterObservableList));
        pListView.getItems().addListener((ListChangeListener<HttpParameter>) change -> {
            if (change.next()) {
                var size = httpParameterObservableList.size();
                if (size > 0) {
                    setListViewVisible(pListView, lbNoParameters, true);
                } else {
                    setListViewVisible(pListView, lbNoParameters, false);
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
        final var name = headerName.getText();
        final var value = headerValue.getText();
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
        final var name = parameterName.getText();
        final var value = parameterValue.getText();
        if (!name.isEmpty()) {
            if (!value.isEmpty()) {
                Platform.runLater(() -> {
                    httpParameterObservableList.add(new HttpParameter(name, value));
                    parameterName.clear();
                    parameterValue.clear();
                });
                parameterName.requestFocus();
            } else {
                parameterValue.requestFocus();
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
     * Show or hide list views
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
     * Get header list views
     *
     * @return ListView<Header>
     */
    public ListView<Header> getHeadersListView() {
        return hListView;
    }

    /**
     * Get http parameters list views
     *
     * @return ListView<HttpParameter>
     */
    public ListView<HttpParameter> getParametersListView() {
        return pListView;
    }

    /**
     * Get parameters titled pane
     *
     * @return TitledPane
     */
    TitledPane getParametersPane() {
        return parametersPane;
    }

    /**
     * Get headers titled pane
     *
     * @return TitledPane
     */
    TitledPane getHeadersPane() {
        return headersPane;
    }

    FlowPane getFpParameters() {
        return fpParameters;
    }

    TilePane getTpParameters() {
        return tpParameters;
    }

    TextArea getBodyTextArea() {
        return bodyTextArea;
    }

    /**
     * Get accordion
     *
     * @return Accordion
     */
    Accordion getAccordion() {
        return accordion;
    }
}
