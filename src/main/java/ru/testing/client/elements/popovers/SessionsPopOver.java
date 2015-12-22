package ru.testing.client.elements.popovers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.HttpSettingsController;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;

import java.io.IOException;

/**
 * Sessions pop over
 */
public class SessionsPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionsPopOver.class);

    public SessionsPopOver(MainController mainController) {

        // Pop over settings
        this.setTitle("Sessions");
        this.setHeaderAlwaysVisible(true);
        this.setHideOnEscape(false);
        this.setCornerRadius(0);
        this.setAutoHide(false);
        this.setDetachable(false);
        this.setOpacity(1);
        this.setArrowSize(0);
        this.setOnHidden(event -> mainController.getMainParent().setDisable(false));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/sessions.fxml"));
            loader.setController(new SessionsController(mainController));
            Parent root = loader.load();
            this.setContentNode(root);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
