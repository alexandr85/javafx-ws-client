package ru.testing.client.elements.headers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.HeadersController;

import java.io.IOException;

import static ru.testing.client.MainApp.getMainController;

/**
 * Http setting pop over
 */
public class HeadersPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadersPopOver.class);
    private HeadersController headersController;

    public HeadersPopOver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/popover.http.headers.fxml"));
            Parent root = loader.load();
            headersController = loader.getController();
            setContentNode(root);
        } catch (IOException e) {
            LOGGER.error("Error load headers pop over: {}", e.getMessage());
        }

        // Pop over settings
        setDetachable(false);
        setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
        setOnHidden(event -> {
            getMainController().getHttpSettings().setSelected(false);
            headersController.getHeaderListView().getSelectionModel().clearSelection();
        });
    }

    /**
     * Get headers controller
     *
     * @return HeadersController
     */
    public HeadersController getHeadersController() {
        return headersController;
    }
}
