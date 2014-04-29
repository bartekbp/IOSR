package pl.edu.agh.kp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeStatistics {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.US);

    private int resolution;
    private int width;

    private int[] cells;
    private int lastPosition;
    private int sum;

    private Date startDate;

    public TimeStatistics(int resolution, int width) {
        this.resolution = resolution;
        this.width = width;

        cells = new int[width];
        lastPosition = 0;
    }

    public static String getCurrentDate() {
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public void start(String dateString) throws ParseException {
        startDate = dateFormat.parse(dateString);
    }

    public void report(String dateString) throws ParseException {
        Date date = dateFormat.parse(dateString);
        long diff = date.getTime() - startDate.getTime();
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(diff);
        int position = (seconds / resolution);

        // If the log is older than the last one, assume it's as old as last one
        if (position < lastPosition) {
            position = lastPosition;
        }

        clearCells(lastPosition, position);
        lastPosition = position;
        cells[position % width]++;
        sum++;
    }

    public int getSum(String dateString) throws ParseException {
        Date date = dateFormat.parse(dateString);
        long diff = date.getTime() - startDate.getTime();
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(diff);
        int position = (seconds / resolution);

        // If the log is older than the last one, assume it's as old as last one
        if (position < lastPosition) {
            position = lastPosition;
        }

        clearCells(lastPosition, position);
        lastPosition = position;
        return sum;
    }

    private void clearCells(int lastPos, int newPos) {
        for (int i = lastPos + 1; i <= newPos; i++) {
            sum -= cells[i % width];
            cells[i % width] = 0;
        }
    }
}
