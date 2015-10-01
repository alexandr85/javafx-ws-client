package ru.testing.client.websocket;

import com.sun.scenario.effect.impl.prism.PrImage;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.gui.Dialogs;

import javax.websocket.*;
import java.net.URI;

/**
 * WebSocket client
 */
@ClientEndpoint
public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static final int NORMAL_CLOSE_CODE = 1000;
    private Session session;
    private MessageHandler messageHandler;

    public Client(final URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(final Session session) {
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

    @OnMessage
    public void onMessage(final String message) {
        if (messageHandler != null) {
            messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(final MessageHandler msgHandler) {
        messageHandler = msgHandler;
    }

    public void sendMessage(final String message) {
        session.getAsyncRemote().sendText(message);
    }

    /**
     * Get connection session
     * @return Session
     */
    public Session getSession() {
        return this.session;
    }
}
