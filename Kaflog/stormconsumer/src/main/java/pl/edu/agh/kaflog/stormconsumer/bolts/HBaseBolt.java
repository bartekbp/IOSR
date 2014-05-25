package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Map;

public class HBaseBolt implements IRichBolt {

    private final String tableName;
    private HTable table;

    public HBaseBolt(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        Configuration configuration = HBaseConfiguration.create();
        try {

            table = new HTable(configuration, tableName);
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
        int size = input.getValues().size();

        Put put = new Put(Bytes.toBytes(rowId));
        for (int i = 1; i < size; ++i) {
            HBaseField field = (HBaseField) input.getValue(i);
            put.add(Bytes.toBytes(field.family), Bytes.toBytes(field.qualifier), Bytes.toBytes(field.value));
        }
        try {
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

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
