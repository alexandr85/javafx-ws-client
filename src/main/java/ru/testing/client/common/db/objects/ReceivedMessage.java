package ru.testing.client.common.db.objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.elements.message.ReceivedMessageType;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static ru.testing.client.common.Utils.getDateFormat;

/**
 * Class described output message object
 */
public class ReceivedMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivedMessage.class);

    private Date time;
    private String message;
    private ReceivedMessageType type;

    public ReceivedMessage(ReceivedMessageType type, String message) {
        this.type = type;
        this.time = Calendar.getInstance().getTime();
        this.message = message;
    }

    public ReceivedMessage(ReceivedMessageType type, String time, String message) {
        this.type = type;
        this.message = message;
        try {
            this.time = getDateFormat().parse(time);
        } catch (ParseException e) {
            LOGGER.error("Error parse time from string: {}", e.getMessage());
            this.time = Calendar.getInstance().getTime();
        }
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
