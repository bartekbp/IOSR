package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.util.*;


public class BucketTimeAggregator extends BaseRichBolt {
    private OutputCollector collector;
    private List<String> fields;
    private Map<String, Integer> data;

    public BucketTimeAggregator(String... fields) {
        this.fields = Arrays.asList(fields);
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        data = new HashMap<String, Integer>();
    }

    @Override
    public void execute(Tuple input) {
        String bucket = input.getStringByField(StormFields.BUCKET);
        FieldsKey key = new FieldsKey(input, fields);
        Integer value;
        String rowId = key.getRowId(bucket);
        if (!data.keySet().contains(rowId)) {
            value = 1;
        } else {
            value = 1 + data.get(rowId);
        }
        data.put(rowId, value);
        List<Object> toEmit = new ArrayList<Object>();
        toEmit.add(rowId);
        toEmit.add(Lists.<Object>newArrayList(new HBaseField("value", "", String.valueOf(value))));
        //System.out.println(toEmit);
        collector.emit(toEmit);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.ROW_ID, StormFields.VALUES));
    }
}
