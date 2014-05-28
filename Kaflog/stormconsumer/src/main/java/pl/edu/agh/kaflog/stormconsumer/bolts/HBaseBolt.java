package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableDescriptors;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HBaseBolt extends BaseRichBolt {

    private final HTDescriptor tableDescriptor;
    private final Properties properties;
    private HTable table;

    public HBaseBolt(HTDescriptor tableDescriptor, Properties properties) {
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

    /**
     * @param input following format: RowId, HBaseField...
     */
    @Override
    public void execute(Tuple input) {
        String rowId = input.getString(0);

        Put put = new Put(Bytes.toBytes(rowId));

        List<HBaseField> fields = (List<HBaseField>) input.getValue(1);
        for (HBaseField field : fields) {
            put.add(Bytes.toBytes(field.family), Bytes.toBytes(field.qualifier), Bytes.toBytes(field.value));
        }
        try {
            System.out.println("Put: " + put);
            table.put(put);
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
