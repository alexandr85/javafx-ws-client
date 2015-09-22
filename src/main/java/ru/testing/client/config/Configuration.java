package ru.testing.client.config;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Configuration parameters for use application
 */
@Parameters(separators = "=")
public class Configuration {

    @Parameter(
            required = true,
            names = {"-a", "--app-type"},
            description = "Set application type console|gui"
    )
    private ApplicationType type = ApplicationType.gui;

    @Parameter(
            names = {"-s", "--server-url"},
            description = "Server url for WebSocket connection (ws[s]://host:port/path/to/websocket)"
    )
    private String serverUrl;

    @Parameter(
            names = {"-t", "--timeout"},
            description = "Connection timeout"
    )
    private int timeout = 5000;

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
    public ApplicationType getApplicationType() {
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
     * Connection timeout
     * @return int (default: 5000)
     */
    public int getTimeout() {
        return timeout;
    }
}
