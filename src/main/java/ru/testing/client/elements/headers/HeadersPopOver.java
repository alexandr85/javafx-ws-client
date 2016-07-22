package ru.testing.client.elements.headers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    public HeadersPopOver(MainController main) {

        // Pop over settings
        setDetachable(false);
        setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
        setOnHidden(event -> main.getHttpSettings().setSelected(false));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/http.fxml"));
            headersController = new HeadersController(main);
            loader.setController(headersController);
            Parent root = loader.load();
            setContentNode(root);
        } catch (IOException e) {
            LOGGER.error("Error load headers pop over: {}", e.getMessage());
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
}
