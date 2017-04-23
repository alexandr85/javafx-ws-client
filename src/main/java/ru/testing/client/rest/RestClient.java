package ru.testing.client.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Http rest client
 */
public class RestClient {

    private static final int TIMEOUT = 30000;
    private Client restClient;

    public RestClient() {
        restClient = Client.create();
        restClient.setConnectTimeout(TIMEOUT);
        restClient.setReadTimeout(TIMEOUT);
    }

    public WebResource getResource(String url) {
        return restClient.resource(url);
    }
}
