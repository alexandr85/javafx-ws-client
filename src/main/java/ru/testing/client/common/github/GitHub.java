package ru.testing.client.common.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.AppProperties;
import ru.testing.client.elements.Dialogs;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

/**
 * Git hub info
 */
public class GitHub extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHub.class);
    private static final int TIMEOUT = 30000;
    private Client client;
    private double lastVersion;
    private AppProperties properties;

    /**
     * Constructor get tags info from git hub
     */
    public GitHub(AppProperties properties) {
        this.properties = properties;
        start();
    }

    /**
     * Run get git hub info
     */
    public void run() {
        String url = properties.getTagsUrl();
        try {
            if (!url.isEmpty()) {
                List<TagInfo> tags = createRequest();
                setLastVersion(Double.valueOf(tags.get(0).getName().replaceAll("v","")));
            }
            if (properties.getVertion() < getLastVersion()) {
                Platform.runLater(() -> Dialogs.getWarningDialog("New version is available! Please, update client"));
            }
            LOGGER.debug("Last version on git hub: {}", getLastVersion());
        } catch (IOException | NumberFormatException e) {
            LOGGER.error(e.getMessage());
            setLastVersion(1.0);
        }
    }

    /**
     * Get rest client
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
     * @return List<TagInfo>
     * @throws IOException
     */
    private List<TagInfo> createRequest() throws IOException {
        WebResource resource = getClient().resource(properties.getTagsUrl());
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.getEntity(String.class), new TypeReference<List<TagInfo>>(){});
    }

    /**
     * Get last tag version from git hub
     * @return Double
     */
    public double getLastVersion() {
        return lastVersion;
    }

    /**
     * Set last tag version
     * @param lastVersion double
     */
    private void setLastVersion(double lastVersion) {
        this.lastVersion = lastVersion;
    }
}