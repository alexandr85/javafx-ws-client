package ru.testing.client.common.github;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.elements.Dialogs;

import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;


/**
 * Git hub info
 */
public class ReleaseChecker extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseChecker.class);
    private static final int TIMEOUT = 30000;
    private static ReleaseChecker instance;
    private AppProperties properties = AppProperties.getAppProperties();
    private String lastVersion = "1.0";
    private Client client;

    private ReleaseChecker() {

    }

    public static ReleaseChecker getInstance() {
        if (instance == null) {
            instance = new ReleaseChecker();
        }
        return instance;
    }

    /**
     * Run get git hub info
     */
    public void run() {
        String url = properties.getTagsUrl();
        try {
            if (!url.isEmpty()) {
                TagInfo[] tags = getTagsFromApi();
                setLastVersion(tags[0].getName().replace("v", ""));
            }
            if (isCurrentVersionOld(properties.getVersion(), lastVersion)) {
                Platform.runLater(() -> {
                    boolean goToPage = new Dialogs().getConfirmationDialog("Great news!",
                            String.format("New version `%s` is available! Go to new release page?", lastVersion));
                    if (goToPage && Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(new URI(properties.getLastReleaseUrl()));
                        } catch (URISyntaxException | IOException e) {
                            LOGGER.error("Error open new release web page");
                        }
                    }
                });
            }
            LOGGER.debug("Last release version on git hub: {}", getLastVersion());
        } catch (IOException | NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Get rest client
     *
     * @return com.sun.jersey.api.client.Client
     */
    private Client getClient() {
        if (client == null) {
            client = Client.create();
            client.setConnectTimeout(TIMEOUT);
            client.setReadTimeout(TIMEOUT);
        }
        return client;
    }

    /**
     * Create request
     *
     * @return List<TagInfo>
     * @throws IOException mapping TagInfo
     */
    private TagInfo[] getTagsFromApi() throws IOException {
        WebResource resource = getClient().resource(properties.getTagsUrl());
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        return new Gson().fromJson(response.getEntity(String.class), TagInfo[].class);
    }

    /**
     * Get last tag version from git hub
     *
     * @return String
     */
    public String getLastVersion() {
        return lastVersion;
    }

    /**
     * Set last tag version
     *
     * @param lastVersion String
     */
    private void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * Compare current and latest git hub versions
     *
     * @param currentVersion String
     * @param newVersion String from git hub
     * @return boolean compare status
     */
    public boolean isCurrentVersionOld(String currentVersion, String newVersion) {
        int[] cvt = Arrays.stream(currentVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] nvt = Arrays.stream(newVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (cvt[0] > nvt[0]) {
            return false;
        } else if (cvt[0] < nvt[0]) {
            return true;
        } else if (cvt[1] < nvt[1]) {
            return true;
        }
        return false;
    }
}
