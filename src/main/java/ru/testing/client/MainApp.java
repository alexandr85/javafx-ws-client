package ru.testing.client;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.config.ApplicationType;
import ru.testing.client.config.Configuration;
import ru.testing.client.gui.MainController;
import ru.testing.client.websocket.Client;

import javax.websocket.MessageHandler;
import java.net.URI;
import java.util.Scanner;

/**
 * Main application class
 */
public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    private static final double PRIMARY_STAGE_MIN_WIDTH = 680;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 490;

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
            parser.usage();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        loader.setController(new MainController(primaryStage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(getClass().getResource("/styles/main.css").toExternalForm());
        primaryStage.setOnCloseRequest((event -> {
            Platform.exit();
            System.exit(0);
        }));
        try {
            Image image = new Image(getClass().getResource("/images/icon.png").toExternalForm());
            if (image.getHeight() == 0) {
                throw new Exception();
            }
            primaryStage.getIcons().addAll(image);
        } catch (Exception e) {
            LOGGER.debug("Icon not found");
        }
        primaryStage.setTitle("WebSocket client");
        primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
        primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

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
                    client.getSession().close();
                    break;
                }
                client.sendMessage(sendMessage);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
