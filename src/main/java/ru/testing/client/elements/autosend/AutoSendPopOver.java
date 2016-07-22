package ru.testing.client.elements.autosend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.AutoSendController;
import ru.testing.client.controllers.MainController;

import java.io.IOException;

/**
 * Sessions pop over
 */
public class AutoSendPopOver extends PopOver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSendPopOver.class);
    private static final double WIDTH = 420.0;
    private static final double HEIGHT = 190.0;
    private AutoSendController controller;

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
        this.setOnHidden(event -> {
            main.getMainParent().setDisable(false);
            main.getHeadersCount().requestFocus();
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/auto-send.fxml"));
            controller = new AutoSendController(main);
            loader.setController(controller);
            Parent root = loader.load();
            this.setContentNode(root);
        } catch (IOException e) {
            LOGGER.error("Error load auto send pop over: {}", e.getMessage());
        }
    }

    /**
     * Get pop over width
     * @return double
     */
    public double getPopOverWidth() {
        return WIDTH;
    }

    /**
     * Get pop over height
     * @return double
     */
    public double getPopOverHeight() {
        return HEIGHT;
    }

    /**
     * Get auto send controller
     * @return AutoSendController
     */
    public AutoSendController getController() {
        return controller;
    }
}
