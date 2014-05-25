package pl.edu.agh.kaflog.producer.kafka;

import java.util.concurrent.TimeUnit;

public class TimeStatistics {
    private int resolution;
    private int width;

    private int[] cells;
    private int lastPosition;
    private int sum;

    private long startTime;

    public TimeStatistics(int resolution, int width) {
        this.resolution = resolution;
        this.width = width;

        cells = new int[width];
        lastPosition = 0;
    }

    public synchronized void start(long startDate) {
        this.startTime = startDate;
    }

    public synchronized void report(long time) {
        long diff = time - startTime;
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

    public synchronized int getSum(long time) {
        long diff = time - startTime;
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
