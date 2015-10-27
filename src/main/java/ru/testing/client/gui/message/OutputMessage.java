package ru.testing.client.gui.message;

import ru.testing.client.commons.MessageType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class described output message object
 */
public class OutputMessage {

    private static final String TIME_FORMAT = "HH:mm:ss.SSS";
    private Date time;
    private String message;
    private MessageType type;

    public OutputMessage(MessageType type, String message) {
        Calendar calendar = Calendar.getInstance();
        this.type = type;
        this.time = calendar.getTime();
        this.message = message;
    }

    /**
     * Get text message from cell
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get message cell time
     * @return String long
     */
    public long getMilliseconds() {
        return time.getTime();
    }

    /**
     * Get formatted time string for display in cell
     * @return String
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(time);
    }

    /**
     * Get cell message type
     * @return MessageType
     */
    public MessageType getMessageType() {
        return type;
    }
}
