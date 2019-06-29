package ru.testing.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.controlsfx.tools.Platform;
import ru.testing.client.common.github.ReleaseChecker;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.controllers.MainController;

import javax.swing.*;
import java.io.IOException;

import static org.controlsfx.tools.Platform.OSX;

/**
 * Main application class
 */
public class FXApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(FXApp.class);
    private static final double PRIMARY_STAGE_MIN_WIDTH = 730;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 540;
    private static Stage primaryStage;
    private static MainController mainController;

    /**
     * Entry point to application
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            LOGGER.error(String.format("Running exception: %s", e.getMessage()));
            System.exit(1);
        }
    }

    /**
     * Get primary stage
     *
     * @return Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Get main controller instance
     *
     * @return MainController
     */
    public static MainController getMainController() {
        return mainController;
    }

    /**
     * Start javafx application window
     *
     * @param primaryStage Stage
     */
    @Override
    public void start(Stage primaryStage) {
        FXApp.primaryStage = primaryStage;
        try {
            AppProperties props = AppProperties.getInstance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent root = loader.load();
            mainController = loader.getController();
            Scene scene = new Scene(root);
            setApplicationIcon(primaryStage, mainController);
            primaryStage.setTitle(String.format("WebSocket & Rest client v%s", props.getVersion()));
            primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
            primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(true);
            primaryStage.show();

            if (props.getSettings().isCheckUpdate()) {
                new ReleaseChecker(false).start();
            }
        } catch (IOException e) {
            LOGGER.error("Error load main fxml view");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Set application icon
     *
     * @param stage Stage
     */
    private void setApplicationIcon(Stage stage, MainController controller) {
        try {
            if (Platform.getCurrent() == OSX) {
                java.awt.Image imageForMac = new ImageIcon(getClass().getResource("/images/icon-512.png")).getImage();
                com.apple.eawt.Application.getApplication().setDockIconImage(imageForMac);

                // Menu bar position for mac os
                controller.getMenuBar().setUseSystemMenuBar(true);
                controller.getExitAppMenu().setVisible(false);
            } else {
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon-16.png")));
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon-32.png")));
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon-64.png")));
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon-128.png")));
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon-256.png")));
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon-512.png")));
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Error load application icon: %s", e.getMessage()));
        }
    }
}
