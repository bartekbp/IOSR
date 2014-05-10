package pl.edu.agh.kaflog.stormconsumer;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import com.sun.net.httpserver.HttpServer;
import pl.edu.agh.kaflog.stormconsumer.bolts.PrintingBolt;
import pl.edu.agh.kaflog.stormconsumer.httpServer.StormHttpHandler;
import pl.edu.agh.kaflog.stormconsumer.spouts.KaflogSpout;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.net.InetSocketAddress;


public class KaflogStorm {

    private static String lastLog;


    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("logs", new KaflogSpout(KaflogProperties.getProperties()));
        builder.setBolt("print", new PrintingBolt()).shuffleGrouping("logs");

        Config conf = new Config();
        conf.put(Config.TOPOLOGY_DEBUG, true);
        conf.setDebug(true);


        String topologyName = KaflogProperties.getProperty("kaflog.storm.topologyName");

        if(KaflogProperties.getBoolProperty("kaflog.storm.useLocalCluster")) {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(topologyName, conf, builder.createTopology());
        } else {
            StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
        }

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

}