package pl.edu.agh.kaflog.master.logs;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

/**
 * Created by lopiola on 26.05.14.
 * Configures kafka stream
 */
@Component
public class LogStreamMaster {
    public static final int NUM_PARTITIONS = 2;
    private static final int LOG_QUEUE_SIZE = 500;
    private static final Logger log = LoggerFactory.getLogger(LogStreamMaster.class);

    private ConsumerConnector consumer;

    private LogStreamConsumer[] consumers;

    private LogQueue logQueue = new LogQueue(LOG_QUEUE_SIZE);

    public LinkedList<LogMessage> pollLogs(long since, int limit) {
        return logQueue.poll(since, limit);
    }

    public LogStreamMaster() {
        String topic = KaflogProperties.getProperty("kaflog.kafka.topic");
        Properties props = new Properties();
        props.put("zookeeper.connect", KaflogProperties.getProperty("kaflog.kafka.zookeeper"));
        props.put("group.id", "master_mon");
        props.put("zk.sessiontimeout.ms", "400");
        props.put("zk.synctime.ms", "200");
        props.put("autocommit.interval.ms", "1000");
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, LogStreamMaster.NUM_PARTITIONS);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);

        consumers = new LogStreamConsumer[NUM_PARTITIONS];
        for (int i = 0; i < NUM_PARTITIONS; i++) {
            consumers[i] = new LogStreamConsumer(consumerMap.get(topic).get(i), logQueue);
        }
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Starting");
        for (int i = 0; i < NUM_PARTITIONS; i++) {
            consumers[i].start();
        }
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Finishing");
        for (int i = 0; i < NUM_PARTITIONS; i++) {
            consumers[i].terminate();
        }
    }
}