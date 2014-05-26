package pl.edu.agh.kaflog.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lopiola on 20.05.14.
 */
public class KaflogDateUtils {
    public static final SimpleDateFormat dateFormatWithYear = new SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.US);
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.US);

    public static long getCurrentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    public static long dateToMillis(String dateString) {
        long result = 0;
        try {
            if (!dateString.startsWith("2014 ")) {
                dateString = "2014 " + dateString;
            }
            result = dateFormatWithYear.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String millisToDate(long millis) {
        return dateFormat.format(millis);
    }

    public static String[] millisToDateAndTime(long millis) {
        String date = dateFormat.format(millis);
        return new String[]{date.substring(0, 6), date.substring(7, 15)};
    }
}
