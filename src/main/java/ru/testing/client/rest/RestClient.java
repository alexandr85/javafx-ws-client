package ru.testing.client.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.db.objects.Header;

import java.util.List;

/**
 * Http rest client
 */
public class RestClient {

    private static final int TIMEOUT = 30000;
    private WebResource.Builder requestBuilder;
    private HttpTypes type;
    private String url;

    public RestClient(String url) {
        this.url = url;
        Client restClient = Client.create();
        restClient.setConnectTimeout(TIMEOUT);
        restClient.setReadTimeout(TIMEOUT);
        requestBuilder = restClient.resource(url).getRequestBuilder();
    }

    public void setHttpType(HttpTypes type) {
        this.type = type;
    }

    public HttpTypes getHttpType() {
        return type;
    }

    public void setHeaders(List<Header> headers) {
        headers.forEach(header -> requestBuilder.header(header.getName(), header.getValue()));
    }

    public ClientResponse execute() {
        switch (type) {
            case HTTP_GET:
                return requestBuilder.get(ClientResponse.class);
            case HTTP_POST:
                return requestBuilder.post(ClientResponse.class);
            default:
                return null;
        }
    }

    public WebResource.Builder getRequestBuilder() {
        return requestBuilder;
    }

    public String getUrl() {
        return url;
    }
}
