package pl.edu.agh.kaflog.common;


import kafka.serializer.Decoder;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

import java.io.*;

public class LogMessageSerializer implements Encoder<LogMessage>, Decoder<LogMessage> {
    public LogMessageSerializer() {
    }

    public LogMessageSerializer(VerifiableProperties properties) {
    }

    /**
     * Kafka needs it.
     * Without it:
     * Exception in thread "main" java.lang.NoSuchMethodException: pl.edu.agh.kaflog.common.LogMessageSerializer.<init>(kafka.utils.VerifiableProperties)
     * @param properties
     */
    public LogMessageSerializer(VerifiableProperties properties) {

    }

    public LogMessageSerializer() {

    }

    @Override
    public byte[] toBytes(LogMessage logMessage) {
        byte[] result = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(logMessage);
            result = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return result;
    }

    @Override
    public LogMessage fromBytes(byte[] bytes) {
        LogMessage result = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            result = (LogMessage) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return result;
    }
}
