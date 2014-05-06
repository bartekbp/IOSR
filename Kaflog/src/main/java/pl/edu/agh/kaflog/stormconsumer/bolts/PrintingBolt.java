package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import pl.edu.agh.kaflog.stormconsumer.KaflogStorm;


public class PrintingBolt extends BaseBasicBolt {


    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        System.out.println("bolt got input");
        System.out.println(input);
        KaflogStorm.setLastLog(input.getString(0));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}