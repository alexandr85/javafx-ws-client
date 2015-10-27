package ru.testing.client.websocket;

import javafx.application.Platform;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.gui.tools.Dialogs;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

/**
 * WebSocket client
 */
public class Client extends Endpoint{

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static final int NORMAL_CLOSE_CODE = 1000;
    private Session session;

    /**
     * Default client constructor
     * @param endpointURI java.net.URI
     */
    public Client(final URI endpointURI) throws Exception {
        ClientManager client = ClientManager.createClient();
        final ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                .decoders(Collections.singletonList(SimpleDecoder.class))
                .encoders(Collections.singletonList(SimpleEncoder.class))
                .build();
        LOGGER.info("Connecting to {} ...", endpointURI.getHost());
        client.connectToServer(this, config, endpointURI);
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
            if (reason.getCloseCode().getCode() != NORMAL_CLOSE_CODE) {
                Platform.runLater(() -> Dialogs.getWarningDialog(String.format("Connection closed: %s", reason)));
            }
            this.session = null;
        }
    }

    /**
     * Set message handler for output response message
     * @param messageHandler MessageHandler.Whole<String>
     */
    public void setMessageHandler(MessageHandler.Whole<String> messageHandler) {
        if (session != null) {
            session.addMessageHandler(messageHandler);
        }
    }

    /**
     * Send string message to websocket session
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
     * @return boolean
     */
    public boolean isOpenConnection() {
        return session != null && session.isOpen();
    }

    /**
     * Method close current connection
     */
    public void closeConnection() {
        try {
            session.close();
        } catch (IOException e) {
            LOGGER.error("Close connection error: {}", e.getCause());
        }
    }
}
