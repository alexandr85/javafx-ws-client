package ru.testing.client.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocket handler
 */
@WebSocket
public class Handler {

    private static final Logger LOG = Logger.getLogger(Handler.class.getName());
    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        session.setIdleTimeout(-1);
        LOG.info("Connected to websocket");
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        LOG.info("Message: ".concat(message));
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        LOG.log(Level.WARNING, "Error: ".concat(error.getMessage()), error);
        session.close();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.log(Level.WARNING, String.format("WebSocket closed: %d %s", statusCode, reason));
    }
}
