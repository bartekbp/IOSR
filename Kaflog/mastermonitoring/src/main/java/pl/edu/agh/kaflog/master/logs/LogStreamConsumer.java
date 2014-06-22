package pl.edu.agh.kaflog.master.logs;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessageSerializer;


/**
 * Created by lopiola on 19.05.14.
 * It takes message stream from kafka topic and deserialize it and put into logQueue
 */

public class LogStreamConsumer extends Thread {
    KafkaStream<byte[], byte[]> stream;
    private LogQueue logQueue;

    private volatile boolean active = true;

    /**
     * Creates kafka consumer
     * */
    public LogStreamConsumer(KafkaStream<byte[], byte[]> stream, LogQueue logQueue) {
        this.stream = stream;
        this.logQueue = logQueue;
    }

    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (active && it.hasNext()) {
            LogMessage message = new LogMessageSerializer().fromBytes(it.next().message());
            logQueue.push(message);
        }
    }

    /**
     * Stops consumer
     */
    public void terminate() {
        active = false;
    }
}
