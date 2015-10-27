package ru.testing.client.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileOperations {

    public static void logIntoFile (String text) {
        File log = new File("log.txt");
        try {
            FileWriter fileWriter = new FileWriter(log);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.flush();
            bufferedWriter.write(text);
            bufferedWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
