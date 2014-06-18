package pl.edu.agh.kaflog.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lopiola on 20.05.14.
 */
public class KaflogDateUtils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.US);

    /**
     * @return current time in milliseconds since Unix epoch
     */
    public static long getCurrentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    /**
     * Converts time to String with format "MMM dd HH:mm:ss"
     * @param millis since Unix epoch
     * @return formatted string
     */
    public static String millisToDate(long millis) {
        return dateFormat.format(millis);
    }

    /**
     * Convert time to pair of string
     * @param millis since Unix epoch
     * @return pair of string - first with covers month and day, second hour minute and seconds
     */
    public static String[] millisToDateAndTime(long millis) {
        String date = dateFormat.format(millis);
        return new String[]{date.substring(0, 6), date.substring(7, 15)};
    }
}
