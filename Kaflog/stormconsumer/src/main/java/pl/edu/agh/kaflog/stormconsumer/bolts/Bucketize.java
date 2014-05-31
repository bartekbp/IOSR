package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Bucketize extends BaseRichBolt {
    private static final DateFormat format = new SimpleDateFormat("HH\07mm");

    private OutputCollector collector;
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        LogMessage logMessage = (LogMessage) input.getValueByField(StormFields.LOG_MESSAGE);
        List<Object> toEmit = Lists.<Object>newArrayList(
                timestampToBucket(logMessage.getTimestamp()),
                logMessage.getHostname(),
                logMessage.getSeverity());
        collector.emit(toEmit);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.BUCKET, StormFields.HOST, StormFields.SEVERITY));
    }

    private String timestampToBucket(long timestamp) {
        return format.format(new Date(timestamp));
    }
}
