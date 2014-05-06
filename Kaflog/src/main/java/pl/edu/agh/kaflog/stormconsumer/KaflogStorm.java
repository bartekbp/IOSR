package pl.edu.agh.kaflog.stormconsumer;


import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import com.sun.net.httpserver.HttpServer;
import pl.edu.agh.kaflog.stormconsumer.bolts.PrintingBolt;
import pl.edu.agh.kaflog.stormconsumer.httpServer.StormHttpHandler;
import pl.edu.agh.kaflog.stormconsumer.spouts.KaflogSpout;
import pl.edu.agh.kaflog.utils.KaflogProperties;

import java.net.InetSocketAddress;


public class KaflogStorm {

    private static String lastLog;


    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("logs", new KaflogSpout(KaflogProperties.getProperties()));
        builder.setBolt("print", new PrintingBolt()).shuffleGrouping("logs");

        Config conf = new Config();
        conf.setDebug(true);


        String topologyName = KaflogProperties.getProperty("kaflog.storm.topologyName");
        StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/test", new StormHttpHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static String getLastLog() {
        return lastLog;
    }

    public static void setLastLog(String lastLog) {
        KaflogStorm.lastLog = lastLog;
    }





    /*
    private static BrokerHosts brokerHosts;
    private static Properties properties;

    public static void main(String[] args) throws IOException, AlreadyAliveException, InvalidTopologyException, InterruptedException {

        Config stormConfig = new Config();
        stormConfig.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);

        properties = new Properties();
        properties.load(Main.class.getClassLoader().getResourceAsStream("kaflog.properties"));
        brokerHosts = new ZkHosts(properties.getProperty(SpoutProperties.ZOOKEEPER));

        stormConfig.setNumWorkers(Integer.parseInt(properties.getProperty(StormProperties.NUMBER_OF_WORKERS)));
        stormConfig.setMaxTaskParallelism(Integer.parseInt(properties.getProperty(StormProperties.MAX_TASK_PARALLELISM)));
    //   stormConfig.put(Config.NIMBUS_HOST, properties.getProperty(StormProperties.NIMBUS_HOST));
      //  stormConfig.put(Config.NIMBUS_THRIFT_PORT, Integer.parseInt(properties.getProperty(StormProperties.NIMBUS_THRIFT_PORT)));
        stormConfig.put(Config.STORM_ZOOKEEPER_PORT, properties.getProperty(StormProperties.ZOOKEEPER_PORT));
        stormConfig.put(Config.STORM_ZOOKEEPER_SERVERS, Lists.newArrayList(properties.getProperty(StormProperties.ZOOKEEPER_SERVERS)));

        StormTopology stormTopology = buildTopology();
        LocalCluster localCluster = new LocalCluster(stormConfig);
        try {
            localCluster.submitTopology(properties.getProperty(StormProperties.NAME), new Config(), stormTopology);
        }catch (Throwable t) {
            t.printStackTrace();
        }
        Thread.sleep(10000);
        localCluster.killTopology(properties.getProperty(StormProperties.NAME));
        localCluster.shutdown();
}

    private static StormTopology buildTopology() {
        SpoutConfig kafkaConfig = new SpoutConfig(
                brokerHosts,
                properties.getProperty(SpoutProperties.TOPIC), "",
                properties.getProperty(SpoutProperties.CONSUMER_ID));

        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("logs", new KafkaSpout(kafkaConfig), 10);
        builder.setBolt("print", new PrintingBolt()).shuffleGrouping("logs");
        return builder.createTopology();
    }*/
}