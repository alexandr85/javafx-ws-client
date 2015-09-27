package ru.testing.client.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.net.URI;

/**
 * WebSocket client
 */
@ClientEndpoint
public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private Session session = null;
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
            LOGGER.info(String.format("Connection open with server %s", session.getRequestURI()));
            this.session = session;
        }
    }

    @OnClose
    public void onClose(final Session session, final CloseReason reason) {
        if (session.isOpen()) {
            LOGGER.info(String.format("Connection close: %s", reason));
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

    public static interface MessageHandler {
        void handleMessage(String message);
    }

    public Session getSession() {
        return this.session;
    }
}
