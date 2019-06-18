package ru.testing.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.controlsfx.tools.Platform;
import ru.testing.client.common.DataBase;
import ru.testing.client.common.github.ReleaseChecker;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.controllers.MainController;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

import static org.controlsfx.tools.Platform.OSX;

/**
 * Main application class
 */
public class MainApp extends javafx.application.Application {

    private static final Logger LOGGER = Logger.getLogger(MainApp.class);
    private static final double PRIMARY_STAGE_MIN_WIDTH = 730;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 540;
    private static Stage primaryStage;
    private static MainController mainController;

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
        MainApp.primaryStage = primaryStage;
        try {

            // Init database
            DataBase.getInstance();

            AppProperties properties = AppProperties.getAppProperties();
            URL viewMainUrl = getClass().getResource("/views/main.fxml");

            if (viewMainUrl == null) {
                throw new IOException("Not found main views fxml resource");
            }

            FXMLLoader loader = new FXMLLoader(viewMainUrl);
            Parent root = loader.load();
            mainController = loader.getController();
            Scene scene = new Scene(root);
            setApplicationIcon(primaryStage, mainController);
            primaryStage.setTitle(String.format("WebSocket & Rest client v%s", properties.getVersion()));
            primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
            primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(true);
            primaryStage.show();
            new ReleaseChecker().start();
        } catch (IOException e) {
            LOGGER.error("Error start application", e);
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
                java.awt.Taskbar.getTaskbar().setIconImage(imageForMac);

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
            LOGGER.error("Error load application icon", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
