package pl.edu.agh.kaflog.stormconsumer.spouts;


import backtype.storm.spout.SchemeAsMultiScheme;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

import java.io.IOException;
import java.util.Properties;

/**
 * This spout produces tuple from kafka message
 * Translation is defined via {@link pl.edu.agh.kaflog.stormconsumer.spouts.LogMessageSchema}
 */
public class KaflogSpout extends KafkaSpout {

    /**
     * Creates and configures spout using provided properties and
     * {@link pl.edu.agh.kaflog.stormconsumer.spouts.LogMessageSchema} schema
     * @param config has following properties <ul>
     *               <li>kaflog.kafka.zookeeper=zookeeper_host:zookeeper_port</li>
     *               <li>kaflog.kafka.topic=topic_name</li>
     *               <li>kaflog.kafka.consumerId=consumerId</li>
     * </ul>
     */
    public KaflogSpout(Properties config) {
        super(readConfig(config));
    }


    private static SpoutConfig readConfig(Properties properties) {
        SpoutConfig config = new SpoutConfig(
                new ZkHosts(properties.getProperty("kaflog.kafka.zookeeper")),
                properties.getProperty("kaflog.kafka.topic"),
                "",
                properties.getProperty("kaflog.kafka.consumerId"));
        config.scheme = new SchemeAsMultiScheme(new LogMessageSchema());
        return config;
    }
}
