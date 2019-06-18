package ru.testing.client.websocket;

import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.SslContextConfigurator;
import org.glassfish.tyrus.client.SslEngineConfigurator;
import org.glassfish.tyrus.ext.extension.deflate.PerMessageDeflateExtension;
import ru.testing.client.common.objects.Header;
import ru.testing.client.elements.Dialogs;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * WebSocket client
 */
public class WsClient extends Endpoint {

    private static final Logger LOGGER = Logger.getLogger(WsClient.class.getName());
    private final ClientManager client;
    private final ClientEndpointConfig config;
    private SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(new SslContextConfigurator());
    private List<Extension> extensions = new ArrayList<>();
    private URI endpointURI;
    private List<Header> headerList;
    private Session session;
    private boolean sslValidate;

    /**
     * Default client constructor
     */
    public WsClient() {

        // SSL configuration
        sslEngineConfigurator.setHostnameVerifier((host, sslSession) -> true);

        // Create ws client
        client = ClientManager.createClient();

        // Create client configuration
        config = ClientEndpointConfig.Builder.create()
                .decoders(singletonList(SimpleDecoder.class))
                .encoders(singletonList(SimpleEncoder.class))
                .configurator(new ClientEndpointConfig.Configurator() {

                    @Override
                    public void beforeRequest(Map<String, List<String>> headers) {
                        try {
                            if (headerList != null && headerList.size() > 0) {
                                for (Header header : headerList) {
                                    String headerName = header.getName();
                                    if (headers.containsKey(headerName)) {
                                        String value = headers.get(headerName).get(0)
                                                .concat(String.format(";%s", header.getValue()));
                                        headers.put(headerName, singletonList(value));
                                    } else {
                                        headers.put(headerName, singletonList(header.getValue()));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error add headers", e);
                        }

                        // Logging request headers
                        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                            System.out.println(String.format("<- %s", entry));
                        }
                    }

                    @Override
                    public void afterResponse(HandshakeResponse hr) {

                        // Logging response headers
                        Map<String, List<String>> headers = hr.getHeaders();
                        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                            System.out.println(String.format("-> %s", entry));
                        }
                    }
                })
                .extensions(extensions)
                .build();
    }

    /**
     * Set endpoint url
     *
     * @param endpointURI URI
     */
    public void setEndpointURI(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    /**
     * Set request header
     *
     * @param headers List<Header>
     */
    public void setHeaders(List<Header> headers) {
        this.headerList = headers;
    }

    /**
     * Set ssl validate for connection
     *
     * @param sslValidate boolean
     */
    public void setSslValidate(boolean sslValidate) {
        this.sslValidate = sslValidate;
    }

    /**
     * Set compression websocket extension
     *
     * @param withCompression boolean
     */
    public void setWithCompression(boolean withCompression) {
        if (withCompression) {
            extensions.add(new PerMessageDeflateExtension());
        }
    }

    /**
     * Open websocket connection
     *
     * @throws Exception connect to server
     */
    public void openConnection() throws Exception {
        if (session != null && session.isOpen()) {
            LOGGER.warn("Already connected!");
        } else {
            LOGGER.debug(String.format("Connecting to %s ...", endpointURI.getHost()));
            if (endpointURI.getScheme().equals("wss") && !sslValidate) {
                client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
            }
            session = client.connectToServer(this, config, endpointURI);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        if (session.isOpen()) {
            LOGGER.debug(String.format("Connection open with server %s", session.getRequestURI()));
            this.session = session;
        }
    }

    @OnClose
    public void onClose(final Session session, final CloseReason reason) {
        if (!session.isOpen()) {
            LOGGER.debug(String.format("Connection closed with reason %s", reason));
            this.session = null;
        }
    }

    /**
     * Set message handler for output response message
     *
     * @param messageHandler MessageHandler.Whole<String>
     */
    public void setMessageHandler(MessageHandler.Whole<String> messageHandler) {
        if (session != null) {
            session.addMessageHandler(messageHandler);
        }
    }

    /**
     * Send string message to websocket session
     *
     * @param message String
     */
    public void sendMessage(String message) {
        try {
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            new Dialogs().getExceptionDialog(e);
        }
    }

    /**
     * Get connection status
     *
     * @return boolean
     */
    public boolean isOpenConnection() {
        return session != null && session.isOpen();
    }

    /**
     * Method close current connection
     */
    public void closeConnection() {
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                LOGGER.error("Close connection error", e.getCause());
            }
        }
    }
}
