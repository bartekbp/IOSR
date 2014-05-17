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

    public static final String[] FACILITY_STRING = {
            "user-level messages",
            "mail system",
            "system daemons",
            "security/authorization messages",
            "messages generated internally by syslogd",
            "line printer subsystem",
            "network news subsystem",
            "UUCP subsystem",
            "clock daemon",
            "security/authorization messages",
            "FTP daemon",
            "NTP subsystem",
            "log audit",
            "log alert",
            "clock daemon",
            "local0",
            "local1",
            "local2",
            "local3",
            "local4",
            "local5",
            "local6",
            "local7"};

    public static final String[] LEVEL_STRING = {
            "emergency",
            "alert",
            "critical",
            "error",
            "warning",
            "notice",
            "info",
            "debug"};

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
