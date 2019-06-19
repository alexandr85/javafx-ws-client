package ru.testing.client.common.github;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import javafx.application.Platform;
import org.apache.log4j.Logger;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.Dialogs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;


/**
 * Git hub tags versions
 */
public class ReleaseChecker extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ReleaseChecker.class);
    private AppProperties properties = AppProperties.getAppProperties();
    private String lastVersion = "1.0";

    /**
     * Run get git hub info
     */
    public void run() {
        String url = properties.getTagsUrl();
        try {
            if (!url.isEmpty()) {
                TagInfo[] tags = getTagsFromApi();
                if (tags.length > 0) {
                    lastVersion = tags[0].getName().replace("v", "");
                }
            }
            if (isCurrentVersionOld(properties.getVersion(), lastVersion)) {
                Platform.runLater(() -> {
                    boolean goToPage = new Dialogs().getConfirmationDialog("Great news!",
                            String.format("New version %s is available!\nGo to new release page?", lastVersion));
                    if (goToPage && java.awt.Desktop.isDesktopSupported()) {
                        try {
                            java.awt.Desktop.getDesktop().browse(new URI(properties.getLastReleaseUrl()));
                        } catch (URISyntaxException | IOException e) {
                            LOGGER.error("Error open new release web page");
                        }
                    }
                });
            }
            LOGGER.info(String.format("Last release version on git hub v%s ", getLastVersion()));
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
        ClientResponse response = Client.create().resource(properties.getTagsUrl()).get(ClientResponse.class);
        TagInfo[] tags = new TagInfo[0];

        if (response.getStatus() != 200) {
            LOGGER.warn(String.format("Can't get tags from github. Response status code %s", response.getStatus()));
        } else {
            try {
                tags = new Gson().fromJson(response.getEntity(String.class), TagInfo[].class);
            } catch (JsonSyntaxException e) {
                LOGGER.warn("Can't parse response as json", e);
            }
        }

        return tags;
    }

    /**
     * Get last tag version from git hub
     *
     * @return String
     */
    private String getLastVersion() {
        return lastVersion;
    }

    /**
     * Compare current and latest git hub versions
     *
     * @param currentVersion String
     * @param newVersion     String from git hub
     * @return boolean compare status
     */
    private boolean isCurrentVersionOld(String currentVersion, String newVersion) {
        int[] cvt = Arrays.stream(currentVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] nvt = Arrays.stream(newVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (cvt[0] > nvt[0]) {
            return false;
        } else if (cvt[0] < nvt[0]) {
            return true;
        } else return cvt[1] < nvt[1];
    }
}
