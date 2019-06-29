package ru.testing.client.elements.http.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.log4j.Logger;
import org.controlsfx.control.PopOver;
import ru.testing.client.controllers.HttpSettingsController;

import java.io.IOException;

import static ru.testing.client.FXApp.getMainController;

/**
 * Http setting pop over
 */
public class HttpSettingsPopOver extends PopOver {

    private static final Logger LOGGER = Logger.getLogger(HttpSettingsPopOver.class);
    private HttpSettingsController httpSettingsController;

    public HttpSettingsPopOver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popover.http.settings.fxml"));
            Parent root = loader.load();
            httpSettingsController = loader.getController();
            setContentNode(root);
        } catch (IOException e) {
            LOGGER.error(String.format("Error load headers pop over: %s", e.getMessage()));
        }

        // Pop over settings
        setDetachable(false);
        setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
        setOnHidden(event -> {
            getMainController().getHttpSettings().setSelected(false);
            httpSettingsController.getHeadersListView().getSelectionModel().clearSelection();
            httpSettingsController.getParametersListView().getSelectionModel().clearSelection();
        });
    }

    /**
     * Get headers controller
     *
     * @return HttpSettingsController
     */
    public HttpSettingsController getHttpSettingsController() {
        return httpSettingsController;
    }
}
