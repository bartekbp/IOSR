package pl.edu.agh.kaflog.stormconsumer.utils;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedWrapper<T> implements Delayed, Serializable {
    private final long origin;
    private final long delay;
    private final T object;

    public DelayedWrapper(T object, long delayInMilliseconds) {
        this.object = object;
        origin = System.currentTimeMillis();
        delay = delayInMilliseconds;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delay - (System.currentTimeMillis() - origin), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        if (delayed == this) {
            return 0;
        }

        long d = (getDelay(TimeUnit.MILLISECONDS) - delayed.getDelay(TimeUnit.MILLISECONDS));
        return ((d == 0) ? 0 : ((d < 0) ? -1 : 1));
    }

    public T getObject() {
        return object;
    }
}
