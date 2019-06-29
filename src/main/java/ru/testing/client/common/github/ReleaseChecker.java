package ru.testing.client.common.github;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import javafx.application.Platform;
import org.apache.log4j.Logger;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.Dialogs;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;


/**
 * Git hub tags versions
 */
public class ReleaseChecker extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ReleaseChecker.class);
    private AppProperties props = AppProperties.getInstance();
    private String lastVersion;
    private boolean isManual;

    public ReleaseChecker(boolean isManual) {
        this.isManual = isManual;
    }

    /**
     * Run get git hub info
     */
    public void run() {
        lastVersion = props.getVersion();

        try {
            TagInfo[] tags = getTagsFromApi();
            if (tags.length > 0) {
                lastVersion = tags[0].getName().replace("v", "");

                LOGGER.debug(String.format("Last release version on git hub: %s", lastVersion));

                if (isCurrentVersionOld(props.getVersion(), lastVersion)) {
                    Platform.runLater(() -> {
                        boolean goToPage = new Dialogs().getConfirmationDialog("Great news!",
                                String.format(
                                        "New version %s is available!\n%s\n%s",
                                        lastVersion,
                                        "Disable auto check in `Settings` if need.",
                                        "Go to new release page?"
                                ));
                        if (goToPage && java.awt.Desktop.isDesktopSupported()) {
                            try {
                                java.awt.Desktop.getDesktop().browse(URI.create(props.getLastReleaseUrl()));
                            } catch (IOException e) {
                                LOGGER.error("Error open new release web page");
                            }
                        }
                    });
                } else if (isManual) {
                    Platform.runLater(() -> new Dialogs().getInfoDialog("You already have the latest version"));
                }
            } else {
                LOGGER.warn("No new tag version from github");
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Create tags request
     *
     * @return List<TagInfo>
     */
    private TagInfo[] getTagsFromApi() {
        TagInfo[] tags = new TagInfo[0];

        try {
            ClientResponse response = Client.create().resource(props.getTagsUrl()).get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new IOException(
                        String.format("response status code %s", response.getStatus())
                );
            }

            tags = new Gson().fromJson(response.getEntity(String.class), TagInfo[].class);
        } catch (IOException | JsonSyntaxException | ClientHandlerException e) {
            LOGGER.warn("Can't get github tags: " + e.getMessage());
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
        int[] cvt = Arrays.stream(currentVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] nvt = Arrays.stream(newVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (cvt[0] > nvt[0]) {
            return false;
        } else if (cvt[0] < nvt[0]) {
            return true;
        } else return cvt[1] < nvt[1];
    }
}
