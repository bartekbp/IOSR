package pl.edu.agh.kaflog.common;


import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;

import java.io.*;
import java.util.Arrays;

public class LogMessage implements Serializable {
    private int facility;
    private int severity;
    private long timestamp;
    private String hostname;
    private String source;
    private String message;

    public LogMessage(int facility, int severity, long timestamp, String hostname, String source, String message) {
        this.facility = facility;
        this.severity = severity;
        this.timestamp = timestamp;
        this.hostname = hostname;
        this.source = source;
        this.message = message;
    }

    @Override
    public String toString() {
        return toString(" ");
    }

    public String toRawString() {
        return toString(Character.toString('\7'));
    }

    private String toString(String sep) {
        String date = KaflogDateUtils.millisToDateAndTime(timestamp)[0];
        String time = KaflogDateUtils.millisToDateAndTime(timestamp)[1];
        return String.format("[%s]" + sep + "%s" + sep + "%s" + sep + "%s" + sep + "%s" + sep + "%s",
                LEVEL_STRING[severity],
                date,
                time,
                hostname,
                source,
                message);
    }

    public static LogMessage fromRawString(String str) {
        return fromTokens(str.split(Character.toString('\07')));
    }

    private static LogMessage fromTokens(String... tokens) {
        String levelName = tokens[0].substring(1, tokens[0].length() - 1);
        int level = Arrays.asList(LEVEL_STRING).indexOf(levelName);
        long timestamp = KaflogDateUtils.dateToMillis(tokens[1] + " " + tokens[2]);
        return new LogMessage(-1, level, timestamp, tokens[3], tokens[4], tokens[5]);
    }

    public int getFacility() {
        return facility;
    }

    public int getSeverity() {
        return severity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return message;
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
}