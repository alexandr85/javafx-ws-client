package ru.testing.client.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.MessageHandler;

/**
 * Console output message handler
 */
public class ConsoleMessageHandler implements MessageHandler.Whole<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger("MessageHandler");

    @Override
    public void onMessage(String message) {
        LOGGER.info("Response: {}", message);
    }
}
