package pl.edu.agh.kaflog.producer;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.producer.kafka.KaflogProducer;
import pl.edu.agh.kaflog.producer.monitoring.MBeanPublisher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private void addTask(Runnable runnable) {
        executorService.submit(runnable);
    }

    private void addTask(final ThrowingRunnable runnable) {
       addTask(new Runnable() {
           @Override
           public void run() {
               try {
                   runnable.run();
               } catch (Exception e) {
                   log.error(e.toString());
               }
           }
       });
    }

    public static void main(String... args) {
        Main main = new Main();
        main.addTask(MBeanPublisher.initTask());
        main.addTask(new KaflogProducer());
    }

    public interface ThrowingRunnable {
        public void run() throws Exception;
    }
}
