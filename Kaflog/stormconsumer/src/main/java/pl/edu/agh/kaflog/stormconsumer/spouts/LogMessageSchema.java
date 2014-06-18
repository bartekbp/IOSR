package pl.edu.agh.kaflog.stormconsumer.spouts;

import backtype.storm.spout.Scheme;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;
import pl.edu.agh.kaflog.common.LogMessageSerializer;
import pl.edu.agh.kaflog.stormconsumer.utils.StormFields;


import java.util.List;

/**
 * Very simple scheme It is necessary to use {@link storm.kafka.KafkaSpout}
 */
public class LogMessageSchema implements Scheme {

    public static final Fields fields = new Fields(StormFields.LOG_MESSAGE);
    private static final LogMessageSerializer SERIALIZER = new LogMessageSerializer();

    /**
     * It is used to transform data from bytes (kafka message) to LogMessage.
     * Uses {@link pl.edu.agh.kaflog.common.LogMessageSerializer}
     * @param ser LogMessage in form of bytes array
     * @return Singleton list with one LogMessageEntry
     */
    @Override
    public List<Object> deserialize(byte[] ser) {
        return Lists.<Object>newArrayList(SERIALIZER.fromBytes(ser));
    }

    /**
     * This are output fields of Kaflog spout
     * @return only One fields containing {@link pl.edu.agh.kaflog.common.LogMessage} object
     */
    @Override
    public Fields getOutputFields() {
        return fields;
    }
}
