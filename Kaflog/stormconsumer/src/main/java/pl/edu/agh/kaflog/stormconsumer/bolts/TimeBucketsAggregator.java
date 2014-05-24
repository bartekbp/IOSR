package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.stormconsumer.model.Bucket;
import pl.edu.agh.kaflog.stormconsumer.model.Kind;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.util.*;

public class TimeBucketsAggregator extends BaseRichBolt {
    private OutputCollector collector;
    private List<String> fields;
    private Map<Bucket, Map<Object, Integer>> counts;

    public TimeBucketsAggregator(String... fields) {
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
        List<HBaseField> toEmit = Collections.singletonList(
                new HBaseField(key.getQualifier(), bucket.toString(), String.valueOf(map.get(key))));
        collector.emit(Lists.newArrayList(key.getRowId(), toEmit));
    }

    private void addAll(Tuple input) {
        FieldsKey key = tupleToKey(input);
        List<Object> toEmit = new ArrayList<Object>();

        for (Bucket bucket : Bucket.values()) {
            Map<Object, Integer> map = counts.get(bucket);
            if (!map.containsKey(key)) {
                map.put(key, 1);
            } else {
                map.put(key, map.get(key) + 1);
            }
            toEmit.add(new HBaseField(key.getQualifier(), bucket.toString(), String.valueOf(map.get(key))));
        }
        collector.emit(Lists.newArrayList(key.getRowId(), toEmit));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.ROW_ID, StormFields.VALUES));
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
