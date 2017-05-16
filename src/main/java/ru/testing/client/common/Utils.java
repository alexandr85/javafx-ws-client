package ru.testing.client.common;

import com.google.gson.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.TabRestController;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Class described utilities methods
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TabRestController.class.getName());
    private static final String TIME_FORMAT = "HH:mm:ss.SSS";

    /**
     * Format diff time from milliseconds
     *
     * @param timeFirst long
     * @param timeLast  long
     * @return String
     */
    public static String getFormattedDiffTime(long timeFirst, long timeLast) {
        long timeDiff = timeLast - timeFirst;
        long ms = timeDiff % 1000;
        long sec = TimeUnit.MILLISECONDS.toSeconds(timeDiff) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff));
        long min = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (min > 29) {
            return "Time diff > 30m";
        } else if (min > 0) {
            return String.format("%dm %ds %dms", min, sec, ms);
        } else if (sec > 0) {
            return String.format("%ds %dms", sec, ms);
        } else {
            return String.format("%dms", ms);
        }
    }

    /**
     * Get simple date formatter
     *
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(TIME_FORMAT);
    }

    /**
     * Try pretty json string from cell message
     *
     * @param message String
     * @return Map.Entry String - message, Boolean - toggle button selected status
     */
    public static PrettyStatus getJsonPretty(String message) {
        try {
            // For sockJs messages
            if (message.startsWith("a[\"{")) {
                message = message.substring(3, message.length() - 2);
            } else if (message.startsWith("[\"{")) {
                message = message.substring(2, message.length() - 2);
            }

            // Try parse json
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(StringEscapeUtils.unescapeJson(message));
            return new PrettyStatus(gson.toJson(jsonElement), true);
        } catch (JsonIOException | JsonSyntaxException e) {
            LOGGER.error("Error pretty json from string: {}", e.getMessage());
            return new PrettyStatus(message, false);
        }
    }

    /**
     * Pretty status object
     */
    public static class PrettyStatus {

        private String message;
        private boolean buttonSelect;

        PrettyStatus(String message, boolean buttonSelect) {
            this.message = message;
            this.buttonSelect = buttonSelect;
        }

        public String getMessage() {
            return message;
        }

        public boolean getButtonSelect() {
            return buttonSelect;
        }
    }
}
