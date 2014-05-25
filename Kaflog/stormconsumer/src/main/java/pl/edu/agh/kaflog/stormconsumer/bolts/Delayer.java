package pl.edu.agh.kaflog.stormconsumer.bolts;


import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.stormconsumer.model.Kind;
import pl.edu.agh.kaflog.stormconsumer.utils.FixedDelayQueue;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Delayer extends BaseRichBolt {
    private Map<Kind, FixedDelayQueue<LogMessage>> queues =
            new HashMap<Kind, FixedDelayQueue<LogMessage>>();
    private List<Thread> threads = new ArrayList<Thread>();
    private AtomicBoolean work = new AtomicBoolean(true);
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        for (Kind kind : Kind.values()) {
            if (kind.getInterval() > 0) {
                queues.put(kind, new FixedDelayQueue<LogMessage>(kind.getInterval()));
            }
        }
        for (final Map.Entry<Kind, FixedDelayQueue<LogMessage>> entry : queues.entrySet()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (work.get()) {
                        LogMessage message = null;
                        try {
                            message = entry.getValue().take();
                        } catch (InterruptedException e) {
                            // exit on next iteration if required
                        }
                        Delayer.this.collector.emit(Lists.<Object>newArrayList(message, entry.getKey()));
                    }
                }
            };
            threads.add(thread);
            thread.start();
        }

    }

    @Override
    public void execute(Tuple tuple) {
        LogMessage message = (LogMessage) tuple.getValueByField(StormFields.LOG_MESSAGE);

        collector.emit(Lists.<Object>newArrayList(message, Kind.CREATED));
        for (FixedDelayQueue<LogMessage> queue : queues.values()) {
            if (queue != null) {
                queue.offer(message);
            }
        }
    }

    @Override
    public void cleanup() {
        work.set(false);
        synchronized (this) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(StormFields.LOG_MESSAGE, StormFields.KIND));
    }
}
