package ru.testing.client.console;

import com.beust.jcommander.JCommander;
import ru.testing.client.config.Configuration;

/**
 * Console testing client
 */
public class ConsoleApp {

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
    }
}
