package pl.edu.agh.kaflog.stormconsumer.spouts;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.common.LogMessageSerializer;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;


import java.util.List;


public class LogMessageSchema implements Scheme {

    public static final Fields fields = new Fields(StormFields.LOG_MESSAGE);
    private static final LogMessageSerializer SERIALIZER = new LogMessageSerializer();

    @Override
    public List<Object> deserialize(byte[] ser) {
        return Lists.<Object>newArrayList(SERIALIZER.fromBytes(ser));
    }

    @Override
    public Fields getOutputFields() {
        return fields;
    }
}
