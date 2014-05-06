package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.stormconsumer.KaflogStorm;
import pl.edu.agh.kaflog.stormconsumer.spouts.LogMessageScheme;


public class PrintingBolt extends BaseBasicBolt {


    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LogMessage message = (LogMessage) input.getValueByField(LogMessageScheme.LOG_MESSAGE_FIELD);
        System.out.println(message);
        KaflogStorm.setLastLog(message.toString());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}