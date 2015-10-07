package ru.testing.client.websocket;

import javafx.application.Platform;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.gui.Dialogs;

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

    public Client(final URI endpointURI) {
        try {
            ClientManager client = ClientManager.createClient();
            final ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                    .decoders(Collections.singletonList(SimpleDecoder.class))
                    .build();
            client.connectToServer(this, config, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
            if (reason.getCloseCode().getCode() != NORMAL_CLOSE_CODE) {
                Platform.runLater(() -> Dialogs.getWarningDialog(String.format("Connection closed: %s", reason)));
            }
            this.session = null;
        }
    }

    public void setMessageHandler(MessageHandler.Whole<String> messageHandler) {
        if (session != null) {
            session.addMessageHandler(messageHandler);
        }
    }

    public void sendMessage(String message) throws IOException {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }

    // todo: need do privet and realization verify connection status
    public Session getSession() {
        return session;
    }
}
