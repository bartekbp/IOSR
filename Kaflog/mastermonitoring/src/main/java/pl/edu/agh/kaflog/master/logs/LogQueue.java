package pl.edu.agh.kaflog.master.logs;

import pl.edu.agh.kaflog.common.LogMessage;

import java.util.LinkedList;

/**
 * Created by lopiola on 19.05.14.
 */
public class LogQueue {
    private LogMessage[] queue;
    private Object[] semaphore;
    private int size;

    // Index of last log in the queue
    private volatile int counter = -1;

    public LogQueue(int size) {
        this.size = size;
        this.queue = new LogMessage[size];
        this.semaphore = new Object[size];
        for (int i = 0; i < size; i++) {
            semaphore[i] = new Object();
        }
    }

    // Puts a log in the queue (on the place of the oldest log)
    // Pushes cannot be done in parallel
    public synchronized void push(LogMessage logMessage) {
        int targetIndex = (counter + 1) % size;
        // Make sure this log is not read/written concurrently
        synchronized (semaphore[targetIndex]) {
            queue[targetIndex] = logMessage;
        }
        counter = targetIndex;
    }

    // Polls all logs that were logged after 'since' param. Up to 'limit' of logs will be returned.
    public LinkedList<LogMessage> poll(long since, int limit) {
        if (limit > size) throw new IllegalArgumentException("limit cannot be bigger than queue size");

        LinkedList<LogMessage> result = new LinkedList<LogMessage>();

        if (counter == -1) return result;

        int index = counter;
        for (int i = 0; i < limit; i++) {
            // Make sure this log is not read/written concurrently
            synchronized (semaphore[index]) {
                if (queue[index] != null && since < queue[index].getTimestamp()) {
                    result.addFirst(queue[index]);
                } else {
                    break;
                }
            }
            index--;
            if (index < 0) {
                index += size;
            }
        }
        return result;
    }
}