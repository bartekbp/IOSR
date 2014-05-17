package pl.edu.agh.kaflog.stormconsumer.spouts;

import backtype.storm.spout.SchemeAsMultiScheme;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

import java.io.IOException;
import java.util.Properties;

public class KaflogSpout extends KafkaSpout {
    public KaflogSpout(Properties properties) throws IOException {
        super(readConfig(properties));
    }

    private static SpoutConfig readConfig(Properties properties) throws IOException {
        SpoutConfig config = new SpoutConfig(
                new ZkHosts(properties.getProperty("kaflog.kafka.zookeeper")),
                properties.getProperty("kaflog.kafka.topic"),
                "",
                properties.getProperty("kaflog.kafka.consumerId"));
        config.scheme = new SchemeAsMultiScheme(new LogMessageScheme());
        return config;
    }
}




