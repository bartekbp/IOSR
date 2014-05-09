package pl.edu.agh.kaflog.utils;


import com.google.common.collect.Lists;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class IteratorUtils {
    public static <T> Iterator<T> fromRemoteIterator(final RemoteIterator<T> iterator) {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                try {
                    return iterator.hasNext();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public T next() {
                try {
                    return iterator.next();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void remove() {
                throw new IllegalStateException();
            }
        };
    }

    public static <T> Iterable<T> toIterable(final Iterator<T> iterator) {
        return Lists.newArrayList(iterator);
    }
}
