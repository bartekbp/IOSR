package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HBaseIncrementingBolt extends BaseRichBolt {

    private final HTDescriptor tableDescriptor;
    private final Properties properties;
    private HTable table;

    public HBaseIncrementingBolt(HTDescriptor tableDescriptor, Properties properties) {
        this.tableDescriptor = tableDescriptor;
        this.properties = properties;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum",
                properties.getProperty("hbase.zookeeper.quorum"));
        configuration.set("hbase.zookeeper.property.clientPort",
                properties.getProperty("hbase.zookeeper.property.clientPort"));
        configuration.set("hbase.master",
                properties.getProperty("hbase.master"));
        try {
            HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
            if(!hBaseAdmin.isTableAvailable(tableDescriptor.name)) {
                hBaseAdmin.createTable(tableDescriptor.getHTableDescriptor());
            }
            table = new HTable(configuration, tableDescriptor.name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Tuple input) {
        String rowId = input.getString(0);

        try {
            table.incrementColumnValue(Bytes.toBytes(rowId), Bytes.toBytes("f"), Bytes.toBytes("count"), 1);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            table.flushCommits();
            table.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
