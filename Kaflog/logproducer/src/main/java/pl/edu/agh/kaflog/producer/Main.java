package pl.edu.agh.kaflog.producer;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;
import pl.edu.agh.kaflog.producer.kafka.KaflogProducer;
import pl.edu.agh.kaflog.producer.monitoring.Pinger;
import pl.edu.agh.kaflog.common.utils.ExecutorUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main implements ExecutorUtils.ThrowingRunnable {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.run();
    }

    @Override
    public void run() throws Exception {
        ExecutorUtils executorUtils = new ExecutorUtils();
        KaflogProducer kaflogProducer = new KaflogProducer();
        executorUtils.addRecurringTask(
                new Pinger(KaflogProperties.getProperty("kaflog.master"), 2997, kaflogProducer),
                5, TimeUnit.SECONDS);
        executorUtils.addTask(kaflogProducer);
    }
}
