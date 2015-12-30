package ru.testing.client.common;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Configuration parameters for use application
 */
@Parameters()
public class Configuration {

    /**
     * Application types running
     * Default gui type
     */
    @Parameter(
            names = {"-a", "--app-type"},
            description = "Set application type"
    )
    private final ApplicationType type = ApplicationType.GUI;

    /**
     * Set application url for console application type
     */
    @Parameter(
            names = {"-u", "--url"},
            description = "Server url for WebSocket connection (ws[s]://host:port/path/to/websocket)"
    )
    private String serverUrl;

    /**
     * Show console application help
     */
    @Parameter(
            names = {"-h", "--help"},
            description = "Show application options",
            help = true
    )
    private boolean help;

    /**
     * Get application type
     * @return String console|gui (default: console)
     */
    public ApplicationType getType() {
        return type;
    }

    /**
     * Get WebSocket server url
     * @return String (ws[s]://host:port/path/to/websocket)
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Show help aplication options
     * @return boolean
     */
    public boolean isHelp() {
        return help;
    }
}
