package pl.edu.agh.kaflog.stormconsumer;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;
import pl.edu.agh.kaflog.stormconsumer.bolts.*;
import pl.edu.agh.kaflog.stormconsumer.spouts.FakeSpout;
import pl.edu.agh.kaflog.stormconsumer.spouts.KaflogSpout;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;


public class Main {

    public static void main(String[] args) throws Exception {

        IRichSpout spout;
        /*if (KaflogProperties.getBoolProperty("kaflog.storm.useFakeSpout")) {
            spout = new FakeSpout();
        } else {
            spout = new KaflogSpout(KaflogProperties.getProperties());
        }*/
        spout = new FakeSpout();
        HTDescriptor descriptor = new HTDescriptor(KaflogProperties.getProperty("kaflog.storm.hbase.table"),
                "$host", "$severity", "$host$severity");

        HTDescriptor severityTable = new HTDescriptor("srm_severity_per_minute", "value");
        HTDescriptor hostTable = new HTDescriptor("srm_host_per_minute", "value");
        HTDescriptor hostSeverityTable = new HTDescriptor("srm_host_severity_per_minute", "value");

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kaflogSpout", spout);
        // buckets
        builder.setBolt("bucketize", new Bucketize()).shuffleGrouping("kaflogSpout");
        builder.setBolt("bucketSeverityAggregator", new BucketTimeAggregator(StormFields.SEVERITY))
                .fieldsGrouping("bucketize", new Fields(StormFields.SEVERITY));
        builder.setBolt("severityStorage", new HBaseBolt(severityTable, KaflogProperties.getProperties()))
                .shuffleGrouping("bucketSeverityAggregator");
        builder.setBolt("bucketHostAggregator", new BucketTimeAggregator(StormFields.HOST))
                .fieldsGrouping("bucketize", new Fields(StormFields.HOST));
        builder.setBolt("hostStorage", new HBaseBolt(hostTable, KaflogProperties.getProperties()))
                .shuffleGrouping("bucketHostAggregator");
      /*  builder.setBolt("bucketHostSeverityAggregator", new BucketTimeAggregator(StormFields.HOST, StormFields.SEVERITY))
                .fieldsGrouping("bucketize", new Fields(StormFields.HOST, StormFields.SEVERITY));
        builder.setBolt("hostSeverityStorage", new HBaseBolt(hostSeverityTable, KaflogProperties.getProperties()))
                .shuffleGrouping("bucketHostSeverityAggregator");*/


//        // real real time part
//        builder.setBolt("delayer", new Delayer()).shuffleGrouping("kaflogSpout");
//        builder.setBolt("extractor", new Extractor()).shuffleGrouping("delayer");
//        builder.setBolt("severityAggregator", new RealTimeAggregator(StormFields.SEVERITY))
//                .fieldsGrouping("extractor", new Fields(StormFields.SEVERITY));
//        builder.setBolt("hostAggregator", new RealTimeAggregator(StormFields.HOST))
//                .fieldsGrouping("extractor", new Fields(StormFields.HOST));
//        builder.setBolt("hostSeverityAggregator", new RealTimeAggregator(StormFields.HOST, StormFields.SEVERITY))
//                .fieldsGrouping("extractor", new Fields(StormFields.HOST, StormFields.SEVERITY));
//        builder.setBolt(
//                "storage", new HBaseBolt(descriptor, KaflogProperties.getProperties()))
//                .shuffleGrouping("hostAggregator")
//                .shuffleGrouping("hostSeverityAggregator")
//                .shuffleGrouping("severityAggregator");

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