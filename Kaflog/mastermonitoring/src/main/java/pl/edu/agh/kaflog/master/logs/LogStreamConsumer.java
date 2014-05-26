package pl.edu.agh.kaflog.master.logs;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import pl.edu.agh.kaflog.common.LogMessageSerializer;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//import pl.edu.agh.kaflog.common.LogMessage;

/**
 * Created by lopiola on 19.05.14.
 */

public class LogStreamConsumer extends Thread {
    KafkaStream<byte[], byte[]> stream;
    private LogQueue logQueue;

    private volatile boolean active = true;

    public LogStreamConsumer(KafkaStream<byte[], byte[]> stream, LogQueue logQueue) {
        this.stream = stream;
        this.logQueue = logQueue;
    }

    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (active && it.hasNext()) {
            logQueue.push(new LogMessageSerializer().fromBytes(it.next().message()));
        }
    }

    public void terminate() {
        active = false;
    }
}
