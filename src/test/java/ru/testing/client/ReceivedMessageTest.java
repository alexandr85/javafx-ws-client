package ru.testing.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.websocket.ReceivedMessageType;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests output message methods
 */
public class ReceivedMessageTest {

    private static final int EXPECTED_TIME_SYMBOLS_COUNT = 13;
    private static final int EXPECTED_FORMATTED_TIME_SYMBOLS_COUNT = 12;
    private static ReceivedMessage receivedMessage;
    private static String message;

    @BeforeAll
    public static void testsSetup() {
        message = "test message";
        receivedMessage = new ReceivedMessage(ReceivedMessageType.SEND, message);
    }

    @Test
    public void testGetMessage() {
        assertEquals(message, receivedMessage.getMessage());
    }

    @Test
    public void testMessageType() {
        assertEquals(ReceivedMessageType.SEND, receivedMessage.getMessageType());
    }

    @Test
    public void testMessageTime() {
        assertEquals(EXPECTED_TIME_SYMBOLS_COUNT, String.valueOf(receivedMessage.getMilliseconds()).length());
    }

    @Test
    public void testMessageFormattedTime() {
        assertEquals(EXPECTED_FORMATTED_TIME_SYMBOLS_COUNT, receivedMessage.getFormattedTime().length());
    }
}
