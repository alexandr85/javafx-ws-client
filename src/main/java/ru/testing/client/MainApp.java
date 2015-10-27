package ru.testing.client;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.commons.ApplicationType;
import ru.testing.client.commons.Configuration;
import ru.testing.client.gui.controllers.MainController;
import ru.testing.client.websocket.Client;

import javax.swing.*;
import javax.websocket.MessageHandler;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Main application class
 */
public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    private static final double PRIMARY_STAGE_MIN_WIDTH = 730;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 540;
    private static final String APP_ICON_URL = "/images/icon.png";

    /**
     * Entry point to application
     * @param args String[]
     */
    public static void main(String[] args) {
        Configuration config = new Configuration();
        JCommander parser = new JCommander();
        parser.setProgramName("java -jar ws.client-${version}.jar");
        parser.addObject(config);
        try {
            parser.parse(args);

            // show help application option
            if (config.isHelp()) {
                parser.usage();
                System.exit(0);
            }

            // select application type
            if (config.getType() == ApplicationType.console) {
                if (config.getServerUrl().isEmpty()) {
                    LOGGER.error("WebSocket server url required");
                    parser.usage();
                    System.exit(1);
                }
                startConsoleClient(config.getServerUrl());
            } else {
                launch(args);
            }
        } catch (Exception e) {
            LOGGER.error("Running exception: {}", e.getMessage());
            parser.usage();
            System.exit(1);
        }
    }

    /**
     * Start javafx application window
     * @param primaryStage Stage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        loader.setController(new MainController(primaryStage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/styles/main.css").toExternalForm());
        setApplicationIcon(primaryStage);
        primaryStage.setTitle("WebSocket client");
        primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
        primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    /**
     * Start console websocket client
     * @param url String
     */
    private static void startConsoleClient(String url) {
        try {
            LOGGER.info("Connecting to {} ...", url);
            final Client client = new Client(new URI(url));
            String sendMessage;
            client.setMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    LOGGER.info("Request: {}", message);
                }
            });
            LOGGER.info("For disconnect from server type 'exit'");
            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Send message: ");
                sendMessage = scanner.nextLine();
                if (sendMessage.equals("exit")) {
                    client.closeConnection();
                    break;
                }
                client.sendMessage(sendMessage);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set application icon
     * @param stage Stage
     */
    private void setApplicationIcon(Stage stage) {
        try {
            URL iconUrl = getClass().getResource(APP_ICON_URL);
            Image image = new Image(iconUrl.toExternalForm());
            if (image.getHeight() == 0) {
                throw new Exception();
            }
            stage.getIcons().addAll(image);
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                java.awt.Image imageForMac = new ImageIcon(iconUrl).getImage();
                com.apple.eawt.Application.getApplication().setDockIconImage(imageForMac);
            }
        } catch (Exception e) {
            LOGGER.error("Icon was not found");
        }
    }
}
