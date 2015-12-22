package ru.testing.client.elements.popovers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.HttpSettingsController;
import ru.testing.client.controllers.MainController;

import java.io.IOException;

/**
 * Http setting pop over
 */
public class HttpSettingPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSettingPopOver.class);
    private ToggleButton httpSettings;
    private TextField serverUrl;

    public HttpSettingPopOver(ToggleButton httpSettings, TextField serverUrl, MainController mainController) {
        this.httpSettings = httpSettings;
        this.serverUrl = serverUrl;

        // Pop over settings
        this.setDetachable(false);
        this.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
        this.setOnHidden(event -> onHiddenAction());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/http-settings.fxml"));
            loader.setController(new HttpSettingsController(mainController));
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
