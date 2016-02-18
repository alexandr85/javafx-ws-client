package ru.testing.client.elements.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static ru.testing.client.common.Utils.getDateFormat;

/**
 * Class described output message object
 */
public class OutputMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputMessage.class);

    private Date time;
    private String message;
    private OutputMessageType type;

    public OutputMessage(OutputMessageType type, String message) {
        this.type = type;
        this.time = Calendar.getInstance().getTime();
        this.message = message;
    }

    public OutputMessage(String time, String message) {
        this.type = OutputMessageType.RECEIVED;
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
     * @return OutputMessageType
     */
    public OutputMessageType getMessageType() {
        return type;
    }
}
