package pl.edu.agh.kaflog.master.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
//import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by lopiola on 19.05.14.
 */
@Component
public class LogStreamConsumer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(LogStreamConsumer.class);

    private LogQueue logQueue = new LogQueue(100);

    private volatile boolean active = true;

    public LinkedList<LogMessage> pollLogs(long since, int limit) {
        return logQueue.poll(since, limit);
    }

    public void terminate() {
        active = false;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Starting");
        start();
    }

    @Override
    public void run() {
        while(active) {
            LogMessage newLogMessage = new LogMessage(
                    1,
                    5,
                    KaflogDateUtils.getCurrentTime(),
                    "kafka-node1",
                    "root",
                    "random logfdghjfhdfghjkfdsfghjlkjhlkgjfhl;kfdjh;ldfkjh;ladhgkajdhgflauhgfiluhaliuerytoiueytoeiwurytwoeiutyajkdfh!" +
            "random logfdghjfhdfghjkfdsfghjlkjhlkgjfhl;kfdjh;ldfkjh;ladhgkajdhgflauhgfiluhaliuerytoiueytoeiwurytwoeiutyajkdfh!");
            logQueue.push(newLogMessage);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Finishing");
        terminate();
    }
}
