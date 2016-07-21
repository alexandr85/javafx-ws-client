package ru.testing.client.elements.autosend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.AutoSendController;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.SessionsController;

import java.io.IOException;

/**
 * Sessions pop over
 */
public class AutoSendPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSendPopOver.class);
    private static final double WIDTH = 420.0;
    private static final double HEIGHT = 190.0;

    public AutoSendPopOver(MainController main) {

        // Pop over settings
        this.setTitle("Send messages after connect");
        this.setHeaderAlwaysVisible(true);
        this.setCornerRadius(0);
        this.setAutoHide(false);
        this.setDetachable(false);
        this.setOpacity(1);
        this.setArrowSize(0);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.setOnHidden(event -> main.getMainParent().setDisable(false));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/auto-send.fxml"));
            loader.setController(new AutoSendController(main));
            Parent root = loader.load();
            this.setContentNode(root);
        } catch (IOException e) {
            LOGGER.error("Error load auto send pop over: {}", e.getMessage());
        }
    }

    public double getPopOverWidth() {
        return WIDTH;
    }

    public double getPopOverHeight() {
        return HEIGHT;
    }
}
