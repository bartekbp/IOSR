package pl.edu.agh.kaflog.stormconsumer.spouts;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;
import kafka.utils.VerifiableProperties;
import pl.edu.agh.kaflog.common.LogMessageSerializer;


import java.util.List;


public class LogMessageScheme implements Scheme {
    public static final String LOG_MESSAGE_FIELD = "logMessage";

    private static final Fields fields = new Fields(LOG_MESSAGE_FIELD);
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
