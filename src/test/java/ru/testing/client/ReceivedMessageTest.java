package ru.testing.client;

import org.junit.Before;
import org.junit.Test;
import ru.testing.client.common.objects.ReceivedMessage;
import ru.testing.client.websocket.ReceivedMessageType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests output message methods
 */
public class ReceivedMessageTest {

    private static final int EXPECTED_TIME_SYMBOLS_COUNT = 13;
    private static final int EXPECTED_FORMATTED_TIME_SYMBOLS_COUNT = 12;
    private ReceivedMessage receivedMessage;
    private String message;

    @Before
    public void testsSetup() {
        message = "test message";
        receivedMessage = new ReceivedMessage(ReceivedMessageType.SEND, message);
    }

    @Test
    public void testGetMessage() {
        assertThat("Get saved message", receivedMessage.getMessage(), equalTo(message));
    }

    @Test
    public void testMessageType() {
        assertThat("Message type is send", receivedMessage.getMessageType(), equalTo(ReceivedMessageType.SEND));
    }

    @Test
    public void testMessageTime() {
        assertThat("Message time in ms class type", receivedMessage.getMilliseconds(), instanceOf(Long.TYPE));
        assertThat("Message time in ms",
                String.valueOf(receivedMessage.getMilliseconds()).length(), equalTo(EXPECTED_TIME_SYMBOLS_COUNT));
    }

    @Test
    public void testMessageFormattedTime() {
        assertThat("Message formatted time class type", receivedMessage.getFormattedTime(), instanceOf(String.class));
        assertThat("Message formatted time length",
                receivedMessage.getFormattedTime().length(), greaterThanOrEqualTo(EXPECTED_FORMATTED_TIME_SYMBOLS_COUNT));
    }
}
