package ru.testing.client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Gui testing client
 */
public class JavaFxApp extends Application {

    private static final double PRIMARY_STAGE_MIN_WIDTH = 590;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 440;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/root.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("WebSocket client");
        primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
        primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
