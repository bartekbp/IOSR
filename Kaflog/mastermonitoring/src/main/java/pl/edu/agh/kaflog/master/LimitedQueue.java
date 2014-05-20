package pl.edu.agh.kaflog.master;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by lopiola on 19.05.14.
 */
public class LimitedQueue<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    public synchronized void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public synchronized boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }

    public synchronized E poll() {
        return removeFirst();
    }

    public synchronized Collection<E> pollAll() {
        LinkedList<E> result = new LinkedList<E>(this);
        clear();
        return result;
    }
}
