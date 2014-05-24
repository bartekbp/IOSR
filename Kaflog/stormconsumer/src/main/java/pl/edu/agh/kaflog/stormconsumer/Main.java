package pl.edu.agh.kaflog.stormconsumer;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;
import pl.edu.agh.kaflog.stormconsumer.bolts.HBaseBolt;
import pl.edu.agh.kaflog.stormconsumer.bolts.CustomAggregator;
import pl.edu.agh.kaflog.stormconsumer.bolts.Delayer;
import pl.edu.agh.kaflog.stormconsumer.bolts.Extractor;
import pl.edu.agh.kaflog.stormconsumer.spouts.FakeSpout;
import pl.edu.agh.kaflog.stormconsumer.spouts.KaflogSpout;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;


public class Main {



    public static void main(String[] args) throws Exception {

        IRichSpout spout;
        if (KaflogProperties.getBoolProperty("kaflog.storm.useFakeSpout")) {
            spout = new FakeSpout();
        } else {
            spout = new KaflogSpout(KaflogProperties.getProperties());
        }

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kaflogSpout", spout);
        builder.setBolt("delayer", new Delayer()).shuffleGrouping("kaflogSpout");
        builder.setBolt("extractor", new Extractor()).shuffleGrouping("delayer");
        builder.setBolt("severityAggregator", new CustomAggregator(StormFields.SEVERITY))
                .fieldsGrouping("extractor", new Fields(StormFields.SEVERITY));
        builder.setBolt("hostAggregator", new CustomAggregator(StormFields.HOST))
                .fieldsGrouping("extractor", new Fields(StormFields.HOST));
        builder.setBolt("hostSeverityAggregator", new CustomAggregator(StormFields.HOST, StormFields.SEVERITY))
                .fieldsGrouping("extractor", new Fields(StormFields.HOST, StormFields.SEVERITY));
        builder.setBolt("storage", new HBaseBolt(KaflogProperties.getProperty("kaflog.storm.hbase.table")))
                .shuffleGrouping("hostAggregator")
                .shuffleGrouping("hostSeverityAggregator")
                .shuffleGrouping("severityAggregator");

        StormTopology topology = builder.createTopology();

        Config conf = new Config();
        conf.put(Config.TOPOLOGY_DEBUG, true);
        conf.setDebug(true);


        String topologyName = KaflogProperties.getProperty("kaflog.storm.topologyName");

        if(KaflogProperties.getBoolProperty("kaflog.storm.useLocalCluster")) {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("delayerTopology", conf, topology);
        } else {
            StormSubmitter.submitTopology(topologyName, conf, topology);
        }
    }
}