package pl.edu.agh.kaflog.stormconsumer;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;
import pl.edu.agh.kaflog.stormconsumer.bolts.Bucketize;
import pl.edu.agh.kaflog.stormconsumer.bolts.HBaseIncrementingBolt;
import pl.edu.agh.kaflog.stormconsumer.bolts.HTDescriptor;
import pl.edu.agh.kaflog.stormconsumer.spouts.FakeSpout;
import pl.edu.agh.kaflog.stormconsumer.spouts.KaflogSpout;


public class Main {

    public static void main(String[] args) throws Exception {

        IRichSpout spout;
        if (KaflogProperties.getBoolProperty("kaflog.storm.useFakeSpout")) {
            spout = new FakeSpout();
        } else {
            spout = new KaflogSpout(KaflogProperties.getProperties());
        }

        HTDescriptor hostSeverityTable = new HTDescriptor("srm_host_severity_per_minute", "f");

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kaflogSpout", spout);
        // buckets
        builder.setBolt("bucketize", new Bucketize()).shuffleGrouping("kaflogSpout");
        builder.setBolt("hostSeverityStorage", new HBaseIncrementingBolt(hostSeverityTable, KaflogProperties.getProperties()))
                .shuffleGrouping("bucketize");


        StormTopology topology = builder.createTopology();

        Config conf = new Config();
        conf.put(Config.TOPOLOGY_DEBUG, true);
        conf.setDebug(true);


        String topologyName = KaflogProperties.getProperty("kaflog.storm.topologyName");

        if(KaflogProperties.getBoolProperty("kaflog.storm.useLocalCluster")) {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("delayerTopology", conf, topology);
        } else {
            conf.put(Config.NIMBUS_HOST, "localhost");
            StormSubmitter.submitTopology(topologyName, conf, topology);
        }
    }
}