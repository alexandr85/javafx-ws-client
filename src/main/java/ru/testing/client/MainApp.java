package ru.testing.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.tools.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.github.GitHub;
import ru.testing.client.controllers.MainController;

import javax.swing.*;
import java.io.IOException;

import static org.controlsfx.tools.Platform.OSX;
import static ru.testing.client.common.properties.AppProperties.getAppProperties;

/**
 * Main application class
 */
public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    private static final double PRIMARY_STAGE_MIN_WIDTH = 730;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 540;

    /**
     * Entry point to application
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            LOGGER.error("Running exception: {}", e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Start javafx application window
     *
     * @param primaryStage Stage
     */
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        MainController controller = new MainController(primaryStage);
        loader.setController(controller);
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            setApplicationIcon(primaryStage, controller);
            primaryStage.setTitle(String.format("WebSocket Client v%s", getAppProperties().getVersion()));
            primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
            primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(true);
            primaryStage.show();
            new GitHub();
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
            LOGGER.error("Error load application icon: {}", e.getMessage());
        }
    }
}
