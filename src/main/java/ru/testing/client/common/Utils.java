package ru.testing.client.common;

import java.util.concurrent.TimeUnit;

/**
 * Class described utilities methods
 */
public class Utils {

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
}
