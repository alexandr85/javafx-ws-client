package ru.testing.client.elements.headers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.HeadersController;
import ru.testing.client.controllers.MainController;

import java.io.IOException;

/**
 * Http setting pop over
 */
public class HeadersPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadersPopOver.class);
    private HeadersController headersController;
    private ToggleButton httpSettings;
    private TextField serverUrl;

    public HeadersPopOver(ToggleButton httpBtn, TextField serverUrl, MainController mainController) {
        this.httpSettings = httpBtn;
        this.serverUrl = serverUrl;

        // Pop over settings
        setDetachable(false);
        setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
        setOnHidden(event -> onHiddenAction());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/http.fxml"));
            headersController = new HeadersController(mainController);
            loader.setController(headersController);
            Parent root = loader.load();
            setContentNode(root);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Get headers controller
     *
     * @return HeadersController
     */
    public HeadersController getHeadersController() {
        return headersController;
    }

    /**
     * Action then pop over hidden
     */
    private void onHiddenAction() {
        httpSettings.setSelected(false);
        serverUrl.requestFocus();
    }
}
