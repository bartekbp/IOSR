package pl.edu.agh.kaflog.master;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;
import pl.edu.agh.kaflog.producer.kafka.KaflogProducer;
import pl.edu.agh.kaflog.utils.ExecutorUtils;

import javax.management.JMException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main implements Runnable {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        Main main = new Main();
        main.run();
    }

    @Override
    public void run() {
        ExecutorUtils executorUtils = new ExecutorUtils();
        executorUtils.addTask(ProducerMonitoring.initTask());

        final ProducerMonitoring producerMonitoring = new ProducerMonitoring();
        executorUtils.addRecurringTask(new ExecutorUtils.ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                System.out.println(producerMonitoring.listClients());
            }
        }, 20, TimeUnit.SECONDS);
    }
}