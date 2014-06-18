package pl.edu.agh.kaflog.stormconsumer.spouts;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * Fake Spout - kaflogSpout mock for testing purposes
 */
public class FakeSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private Random random = new Random();
    private int counter = 0;

    /**
     * Output fields declaration
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.LOG_MESSAGE));
    }

    /**
     * Spout initialization
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    /**
     * generate fake LogMessage and wait for som time (about 1 second in exponential distribution)
     */
    @Override
    public void nextTuple() {
        Date now = new Date();
        LogMessage message = new LogMessage(3, random.nextInt(7), now.getTime(),
                random.nextBoolean()?"localhost": "192.168.0.17",
                "fake app", "fake app message: " + counter++);
        collector.emit(Lists.<Object>newArrayList(message));


        long sleep = (long)(-1000 * Math.log(1 - Math.random()));

        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            //do nothing
        }

    }
}
