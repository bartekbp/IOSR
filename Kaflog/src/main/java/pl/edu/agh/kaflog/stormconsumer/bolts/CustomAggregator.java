package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.stormconsumer.spouts.Kind;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.util.*;

public class CustomAggregator extends BaseRichBolt {
    private OutputCollector collector;
    private List<String> fields;
    private Map<Bucket, Map<Object, Integer>> counts;

    public CustomAggregator(String... fields) {
        this.fields = Arrays.asList(fields);
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        counts = new HashMap<Bucket, Map<Object, Integer>>(Bucket.values().length);
        for(Bucket bucket : Bucket.values()) {
            counts.put(bucket, new HashMap<Object, Integer>());
        }
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        Kind kind = (Kind) input.getValueByField(StormFields.KIND);
        if (kind == Kind.CREATED) {
            addAll(input);
        } else {
            removeFrom(input, revokingKindToBucket(kind));
        }

    }

    private void removeFrom(Tuple input, Bucket bucket) {
        Map<Object, Integer> map = counts.get(bucket);
        FieldsKey key = tupleToKey(input);
        Integer value = map.get(key);
        if (value.equals(1)) {
            map.remove(key);
        } else {
            map.put(key, map.get(key) - 1);
        }
        HBaseField field = new HBaseField(key.getFamily(), bucket.toString(), String.valueOf(map.get(key)));
        collector.emit(Lists.<Object>newArrayList(key.getId(), Lists.newArrayList(field)));

    }

    private void addAll(Tuple input) {
        FieldsKey key = tupleToKey(input);
        List<Object> fields = new ArrayList<>();

        for (Bucket bucket : Bucket.values()) {
            Map<Object, Integer> map = counts.get(bucket);
            if (!map.containsKey(key)) {
                map.put(key, 1);
            } else {
                map.put(key, map.get(key) + 1);
            }
            fields.add(new HBaseField(key.getFamily(), bucket.toString(), String.valueOf(map.get(key))));
        }
        List<Object> toEmit = new ArrayList<>();
        toEmit.add(key.getId());
        toEmit.add(fields);
        collector.emit(toEmit);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.ROW_ID, StormFields.HBASE_FIELDS));

    }

    private FieldsKey tupleToKey(Tuple tuple) {
        return new FieldsKey(tuple, fields);
    }

    private Bucket revokingKindToBucket(Kind kind) {
        switch (kind) {
            case REVOKING_DAY: return Bucket.DAY;
            case REVOKING_HOUR: return Bucket.HOUR;
            case REVOKING_MINUTE: return Bucket.MINUTE;
            default: return null;
        }
    }
}
