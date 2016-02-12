package ru.testing.client;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.tools.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.qatools.properties.PropertyLoader;
import ru.testing.client.common.AppProperties;
import ru.testing.client.common.ApplicationType;
import ru.testing.client.common.Configuration;
import ru.testing.client.common.github.GitHub;
import ru.testing.client.controllers.MainController;
import ru.testing.client.websocket.MessageHandler;
import ru.testing.client.websocket.Client;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import static org.controlsfx.tools.Platform.OSX;

/**
 * Main application class
 */
public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    private static final double PRIMARY_STAGE_MIN_WIDTH = 730;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 540;
    private static AppProperties properties;

    /**
     * Entry point to application
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        Configuration config = new Configuration();
        properties = PropertyLoader.newInstance().populate(AppProperties.class);
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
            if (config.getType() == ApplicationType.CONSOLE) {
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
     *
     * @param primaryStage Stage
     */
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        MainController controller = new MainController(primaryStage, properties);
        loader.setController(controller);
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            setApplicationIcon(primaryStage, controller);
            primaryStage.setTitle(String.format("WebSocket Client v%s", properties.getVersion()));
            primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
            primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(true);
            primaryStage.show();
            new GitHub(properties);
        } catch (IOException e) {
            LOGGER.error("Error load fxml view!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Start console websocket client
     *
     * @param url String
     */
    private static void startConsoleClient(String url) {
        try {
            final Client client = new Client();
            client.setEndpointURI(new URI(url));
            client.openConnection();
            if (client.isOpenConnection()) {
                String sendMessage;
                client.setMessageHandler(new MessageHandler());
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
            }
        } catch (Exception e) {
            e.printStackTrace();
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
