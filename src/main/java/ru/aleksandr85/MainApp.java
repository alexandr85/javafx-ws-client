package ru.aleksandr85;

import com.beust.jcommander.JCommander;

/**
 * Main point to application
 */
public class MainApp {

    public static void main(String[] args) {
        Configuration config = new Configuration();
        JCommander parser = new JCommander();
        parser.setProgramName("java -jar *.jar");
        parser.addObject(config);
        try {
            parser.parse(args);
        } catch (Exception e) {
            parser.usage();
            System.exit(1);
        }
    }
}
