package ru.testing.client.message;

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

    public String getMessage() {
        return message;
    }

    public long getMilliseconds() {
        return time.getTime();
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(time);
    }

    public MessageType getMessageType() {
        return type;
    }
}
