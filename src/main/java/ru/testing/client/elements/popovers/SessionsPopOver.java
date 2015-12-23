package ru.testing.client.elements.popovers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;

import java.io.IOException;

/**
 * Sessions pop over
 */
public class SessionsPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionsPopOver.class);
    private static final double WIDTH = 320.0;
    private static final double HEIGHT = 190.0;

    public SessionsPopOver(MainController mainController) {

        // Pop over settings
        this.setTitle("Sessions");
        this.setHeaderAlwaysVisible(true);
        this.setCornerRadius(0);
        this.setAutoHide(false);
        this.setDetachable(false);
        this.setOpacity(1);
        this.setArrowSize(0);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
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


    public double getPopOverWidth() {
        return WIDTH;
    }

    public double getPopOverHeight() {
        return HEIGHT;
    }
}
