package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Extractor extends BaseRichBolt {
    OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        LogMessage message = (LogMessage) tuple.getValueByField(StormFields.LOG_MESSAGE);

        List<Object> result = new ArrayList<Object>();
        result.add(tuple.getValueByField(StormFields.KIND));
        result.add(message.getHostname());
        result.add(message.getSeverity());
        collector.emit(result);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.KIND, StormFields.HOST, StormFields.SEVERITY));
    }
}
