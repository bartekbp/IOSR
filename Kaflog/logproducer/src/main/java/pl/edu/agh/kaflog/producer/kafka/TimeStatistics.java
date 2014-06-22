package pl.edu.agh.kaflog.producer.kafka;

import java.util.concurrent.TimeUnit;

/**
 * Encapsulate statistics about amount of some events 
 */
public class TimeStatistics {
    private int resolution;
    private int width;

    private int[] cells;
    private int lastPosition;
    private int sum;

    private long startTime;

    /**
     * @param resolution - time cell size in seconds
     * @param width - number of cells
     */
    public TimeStatistics(int resolution, int width) {
        this.resolution = resolution;
        this.width = width;

        cells = new int[width];
        lastPosition = 0;
    }

    /**
     * Start gathering data
     * @param startDate start time in number of milliseconds since Unix epoch 
     */
    public synchronized void start(long startDate) {
        this.startTime = startDate;
    }

    /**
     * report an event occurrence
     * @param time time of an event in number of milliseconds since Unix epoch 
     */
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

    /**
     * getNumber of events
     * @param time time of an event in number of milliseconds since Unix epoch 
     */
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

    /**
     * free unused cells
     * @param lastPos previous last used cell position
     * @param newPos current last used cell position
     */
    private void clearCells(int lastPos, int newPos) {
        for (int i = lastPos + 1; i <= newPos; i++) {
            sum -= cells[i % width];
            cells[i % width] = 0;
        }
    }
}
