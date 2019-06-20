package ru.testing.client.common.github;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import org.apache.log4j.Logger;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.Dialogs;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;


/**
 * Git hub tags versions
 */
public class ReleaseChecker extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ReleaseChecker.class);
    private AppProperties properties = AppProperties.getInstance();
    private String lastVersion = "1.0";

    /**
     * Run get git hub info
     */
    public void run() {
        var url = properties.getTagsUrl();
        try {
            if (!url.isEmpty()) {
                var tags = getTagsFromApi();
                if (tags.length > 0) {
                    lastVersion = tags[0].getName().replace("v", "");
                }
            }
            var needUpdate = isCurrentVersionOld(properties.getVersion(), lastVersion);
            if (needUpdate) {
                Platform.runLater(() -> {
                    var goToPage = new Dialogs().getConfirmationDialog("Great news!",
                            String.format("New version %s is available!\nGo to new release page?", lastVersion)
                    );
                    if (goToPage && java.awt.Desktop.isDesktopSupported()) {
                        try {
                            java.awt.Desktop.getDesktop().browse(URI.create(properties.getLastReleaseUrl()));
                        } catch (IOException e) {
                            LOGGER.error("Error open new release web page");
                        }
                    }
                });
            }
            LOGGER.info(String.format("Last release version on git hub v%s ", lastVersion));
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Create request
     *
     * @return List<TagInfo> github tags info data
     */
    private TagInfo[] getTagsFromApi() {
        var tags = new TagInfo[0];
        var client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(properties.getTagsUrl()))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException(String.format("Can't get tags from github. Response status code %s", response.statusCode()));
            }

            tags = new Gson().fromJson(response.body(), TagInfo[].class);
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            LOGGER.warn(e);
        }

        return tags;
    }

    /**
     * Compare current and latest git hub versions
     *
     * @param currentVersion String
     * @param newVersion     String from git hub
     * @return boolean compare status
     */
    private boolean isCurrentVersionOld(String currentVersion, String newVersion) {
        var cvt = Arrays.stream(currentVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        var nvt = Arrays.stream(newVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (cvt[0] > nvt[0]) {
            return false;
        } else if (cvt[0] < nvt[0]) {
            return true;
        } else return cvt[1] < nvt[1];
    }
}
