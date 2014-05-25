package pl.edu.agh.kaflog.stormconsumer.utils;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class FixedDelayQueue<T> implements Serializable {

    private final long delay;
    private BlockingQueue<DelayedWrapper<T>> pendingQueue = new DelayQueue<DelayedWrapper<T>>();

    public FixedDelayQueue(long delay) {
        this.delay = delay;
    }

    public void offer(T object) {
        pendingQueue.offer(new DelayedWrapper<T>(object, delay));
    }


    public T take() throws InterruptedException {
        DelayedWrapper<T> wrapper = pendingQueue.take();
        return wrapper.getObject();
    }
}
