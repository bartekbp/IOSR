package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.HiveUtils;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Bolt that divide LogMessages that are incoming from kafka to minute buckets
 * E.g. for LogMessage with timestamp with time: 2014/06/01 18:36:12 bucket "18:36" would be created
 */
public class Bucketize extends BaseRichBolt {

    private OutputCollector collector;
    private HiveUtils hiveUtils = new HiveUtils();

    /**
     * Preparation
     * @param collector is used later to emit tuples
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    /**
     * For every incoming tuple it emits new tuple with proper bucket for HBase storage
     * @param input
     */
    @Override
    public void execute(Tuple input) {
        System.out.println(input);
        LogMessage logMessage = (LogMessage) input.getValueByField(StormFields.LOG_MESSAGE);
        List<Object> toEmit = Lists.<Object>newArrayList(
                hiveUtils.toHBaseKey(
                        hiveUtils.toHiveTimestamp(roundToMinutes(logMessage)),
                        logMessage.getHostname(),
                        LogMessage.LEVEL_STRING[logMessage.getSeverity()])
        );
        collector.emit(toEmit);
    }

    private long roundToMinutes(LogMessage logMessage) {
        long bucketTime = 1000 * 60;
        return (logMessage.getTimestamp() / bucketTime) * bucketTime;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.ROWID));
    }
}
