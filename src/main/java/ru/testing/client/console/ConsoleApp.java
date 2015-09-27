package ru.testing.client.console;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.config.Configuration;
import ru.testing.client.websocket.Client;
import java.net.URI;
import java.util.Scanner;

/**
 * Console testing client
 */
public class ConsoleApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleApp.class);

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

        try {
            final Client client = new Client(new URI("ws://echo.websocket.org"));
            String message;
            client.addMessageHandler(serverMessage -> LOGGER.info(String.format("=> %s", serverMessage)));

            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Send message: ");
                message = scanner.nextLine();
                if (message.equals("exit")) {
                    client.getSession().close();
                    break;
                }
                client.sendMessage(message);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
