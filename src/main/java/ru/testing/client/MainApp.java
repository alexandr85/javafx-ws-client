package ru.testing.client;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.testing.client.config.ApplicationType;
import ru.testing.client.config.Configuration;

/**
 * Main application class
 */
public class MainApp extends Application {

    private static final double PRIMARY_STAGE_MIN_WIDTH = 590;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 440;

    /**
     * Entry point to application
     * @param args String[]
     */
    public static void main(String[] args) {
        Configuration config = new Configuration();
        JCommander parser = new JCommander();
        parser.setProgramName("java -jar ws.client.jar");
        parser.addObject(config);
        try {
            parser.parse(args);
        } catch (Exception e) {
            parser.usage();
            System.exit(1);
        }

        // show help application option
        if (config.isHelp()) {
            parser.usage();
            System.exit(0);
        }

        // select application type
        if (config.getType() == ApplicationType.console) {
            System.out.println("Start console application");
        } else {
            launch(args);
        }
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
