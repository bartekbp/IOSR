package pl.edu.agh.kaflog.master.logs;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import kafka.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
//import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessageSerializer;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by lopiola on 19.05.14.
 */
@Component
public class LogStreamConsumer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(LogStreamConsumer.class);

    private LogQueue logQueue = new LogQueue(100);

    private volatile boolean active = true;

    private ConsumerConnector consumer;
    private String topic;

    public LinkedList<LogMessage> pollLogs(long since, int limit) {
        return logQueue.poll(since, limit);
    }

    public LogStreamConsumer() {
        topic = KaflogProperties.getProperty("kaflog.kafka.topic");
        Properties props = new Properties();
        props.put("zookeeper.connect", KaflogProperties.getProperty("kaflog.kafka.zookeeper"));
        props.put("group.id", "master_mon");
        props.put("zk.sessiontimeout.ms", "400");
        props.put("zk.synctime.ms", "200");
        props.put("autocommit.interval.ms", "1000");
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
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
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (active && it.hasNext()) {
            logQueue.push(new LogMessageSerializer().fromBytes(it.next().message()));
        }
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Finishing");
        terminate();
    }
}
