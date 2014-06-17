package pl.edu.agh.kaflog.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class allows to use Executor with our ThrowingRunnable interface instead of build-in Runnable
 * Throwing runnable allows to create runnable tasks that throws compile-time exceptions.
 */
public class ExecutorUtils implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutorUtils.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Submits Runnable task
     * @param runnable
     */
    public void addTask(Runnable runnable) {
        executorService.submit(runnable);
    }

    /**
     * Submits Throwing Runnable task
     * @param runnable
     */
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

    /**
     * Submits all tasks in list
     * @param throwingRunnables list of ThrowingRunnables to submit
     */
    public void addTasks(List<ThrowingRunnable> throwingRunnables) {
        for(ThrowingRunnable throwingRunnable : throwingRunnables) {
            addTask(throwingRunnable);
        }
    }

    /**
     * Submits recurring (cyclic) task
     * @param runnable Task to submit
     * @param time amount of time between firing this task
     * @param unit unit in which time is expressed
     */
    public void addRecurringTask(final ThrowingRunnable runnable, int time, TimeUnit unit) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                addTask(runnable);
            }
        }, 0, unit.toMillis(time));

    }

    /**
     * Stops executing tasks
     */
    @Override
    public void close()  {
        executorService.shutdown();
    }

    /**
     * Interface that allows submiting tasks that throws compile-time exception
     */
    public interface ThrowingRunnable {
        public void run() throws Exception;
    }

}
