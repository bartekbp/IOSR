package pl.edu.agh.kaflog.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutorUtils.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void addTask(Runnable runnable) {
        executorService.submit(runnable);
    }

    public void addTask(final ThrowingRunnable runnable) {
        addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
        });
    }

    public void addRecurringTask(final Runnable runnable, int time, TimeUnit unit) {
        addRecurringTask(new ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                runnable.run();
            }
        }, time, unit);
    }

    public void addRecurringTask(final ThrowingRunnable runnable, int time, TimeUnit unit) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                addTask(runnable);
            }
        }, time, unit.toMillis(1));

    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }

    public interface ThrowingRunnable {
        public void run() throws Exception;
    }

}
