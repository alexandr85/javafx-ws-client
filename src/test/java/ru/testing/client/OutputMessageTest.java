package ru.testing.client;

import org.junit.Before;
import org.junit.Test;
import ru.testing.client.elements.message.OutputMessage;
import ru.testing.client.elements.message.OutputMessageType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests output message methods
 */
public class OutputMessageTest {

    private static final int EXPECTED_TIME_SYMBOLS_COUNT = 13;
    private static final int EXPECTED_FORMATTED_TIME_SYMBOLS_COUNT = 12;
    private OutputMessage outputMessage;
    private String message;

    @Before
    public void testsEntryPoint() {
        message = "test message";
        outputMessage = new OutputMessage(OutputMessageType.SEND, message);
    }

    @Test
    public void testGetMessage() {
        assertThat("Get saved message", outputMessage.getMessage(), equalTo(message));
    }

    @Test
    public void testMessageType() {
        assertThat("Message type is send", outputMessage.getMessageType(), equalTo(OutputMessageType.SEND));
    }

    @Test
    public void testMessageTime() {
        assertThat("Message time in ms class type", outputMessage.getMilliseconds(), instanceOf(Long.TYPE));
        assertThat("Message time in ms",
                String.valueOf(outputMessage.getMilliseconds()).length(), equalTo(EXPECTED_TIME_SYMBOLS_COUNT));
    }

    @Test
    public void testMessageFormattedTime() {
        assertThat("Message formatted time class type", outputMessage.getFormattedTime(), instanceOf(String.class));
        assertThat("Message formatted time length",
                outputMessage.getFormattedTime().length(), greaterThanOrEqualTo(EXPECTED_FORMATTED_TIME_SYMBOLS_COUNT));
    }
}
