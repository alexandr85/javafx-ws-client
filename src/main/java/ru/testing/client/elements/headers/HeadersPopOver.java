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
    private ToggleButton httpSettings;
    private TextField serverUrl;

    public HeadersPopOver(ToggleButton httpBtn, TextField serverUrl, MainController mainController) {
        this.httpSettings = httpBtn;
        this.serverUrl = serverUrl;

        // Pop over settings
        this.setDetachable(false);
        this.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
        this.setOnHidden(event -> onHiddenAction());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/http.fxml"));
            loader.setController(new HeadersController(mainController));
            Parent root = loader.load();
            this.setContentNode(root);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Action then pop over hidden
     */
    private void onHiddenAction() {
        httpSettings.setSelected(false);
        serverUrl.requestFocus();
    }
}
