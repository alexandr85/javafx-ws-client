package ru.testing.client.common.objects;

import ru.testing.client.websocket.ReceivedMessageType;

import java.util.Calendar;
import java.util.Date;

import static ru.testing.client.common.Utils.getDateFormat;

/**
 * Class described output message object
 */
public class ReceivedMessage {

    private Date time;
    private String message;
    private ReceivedMessageType type;

    public ReceivedMessage(ReceivedMessageType type, String message) {
        this.type = type;
        this.time = Calendar.getInstance().getTime();
        this.message = message;
    }

    /**
     * Get text message from cell
     *
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get message cell time
     *
     * @return String long
     */
    public long getMilliseconds() {
        return time.getTime();
    }

    /**
     * Get formatted time string for display in cell
     *
     * @return String
     */
    public String getFormattedTime() {
        return getDateFormat().format(time);
    }

    /**
     * Get cell message type
     *
     * @return ReceivedMessageType
     */
    public ReceivedMessageType getMessageType() {
        return type;
    }

    /**
     * Get ReceivedMessage as string
     *
     * @return String
     */
    public String toString() {
        return String.format("%s %s %s", getMessageType(), getFormattedTime(), getMessage());
    }
}
