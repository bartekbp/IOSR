package pl.edu.agh.kaflog.hadoopconsumer;

import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.coders.MessageDecoder;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessageSerializer;

/**
 * Adapter of {@link pl.edu.agh.kaflog.common.LogMessageSerializer} that lets use it with Camus
 */
public class LogMessageDecoder extends MessageDecoder<byte[], LogMessage> {
    private final LogMessageSerializer logMessageSerializer = new LogMessageSerializer();
    @Override
    public CamusWrapper<LogMessage> decode(byte[] bytes) {
        return new CamusWrapper<LogMessage>(logMessageSerializer.fromBytes(bytes));
    }
}