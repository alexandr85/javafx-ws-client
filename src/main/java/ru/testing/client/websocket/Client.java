package ru.testing.client.websocket;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.elements.headers.Header;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * WebSocket client
 */
public class Client extends Endpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private final ClientManager client;
    private final ClientEndpointConfig config;
    private URI endpointURI;
    private List<Header> headersList;
    private Session session;

    /**
     * Default client constructor
     */
    public Client() {
        client = ClientManager.createClient();
        config = ClientEndpointConfig.Builder.create()
                .decoders(singletonList(SimpleDecoder.class))
                .encoders(singletonList(SimpleEncoder.class))
                .configurator(new ClientEndpointConfig.Configurator() {

                    @Override
                    public void beforeRequest(Map<String, List<String>> headers) {
                        try {
                            if (headersList != null && headersList.size() > 0) {
                                for (Header header : headersList) {
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
                            LOGGER.error("Add headers:", e);
                        }
                    }
                })
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
        this.headersList = headers;
    }

    /**
     * Open websocket connection
     *
     * @throws Exception
     */
    public void openConnection() throws Exception {
        if (session != null && session.isOpen()) {
            LOGGER.warn("Session already connected!");
        } else {
            LOGGER.info("Connecting to {} ...", endpointURI.getHost());
            client.connectToServer(this, config, endpointURI);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        if (session.isOpen()) {
            LOGGER.info("Connection open with server {}", session.getRequestURI());
            this.session = session;
        }
    }

    @OnClose
    public void onClose(final Session session, final CloseReason reason) {
        if (!session.isOpen()) {
            LOGGER.info("Connection closed: {}", reason);
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
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        if (session != null) {
            session.getBasicRemote().sendText(message);
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
                LOGGER.error("Close connection error: {}", e.getCause());
            }
        }
    }
}
