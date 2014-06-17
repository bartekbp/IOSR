package pl.edu.agh.kaflog.common;


import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;

import java.io.*;
import java.util.Arrays;

/**
 * This class encapsulates one Log entry taken from syslog and later processed by this system
 * It is regular Java bean
 */
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
        String date = KaflogDateUtils.millisToDateAndTime(timestamp)[0];
        String time = KaflogDateUtils.millisToDateAndTime(timestamp)[1];
        return String.format("[%s] %s %s %s %s %s",
                LEVEL_STRING[severity],
                date,
                time,
                hostname,
                source,
                message);
    }

    /**
     * Converts LogMessage to string that can be processed by HDFS
     * @return hdfs representation of Log Message
     */
    public String toHdfsFormat() {
        char sep = KaflogConstants.SEPARATOR;
        return String.format("%s" + sep + "%s" + sep + "%s" + sep + "%s" + sep + "%s",
                LEVEL_STRING[severity],
                timestamp / KaflogConstants.MILLISECONDS_IN_SECOND,
                hostname,
                source,
                message);
    }

    /**
     * Converts hdfs compliant string to LogMessage
     * @param str taken from Hive
     * @return equivalent LogMessage
     */
    public static LogMessage fromHive(String str) {
        String[] tokens = str.split(Character.toString(KaflogConstants.SEPARATOR));
        String levelName = tokens[0].substring(1, tokens[0].length() - 1);
        int level = Arrays.asList(LEVEL_STRING).indexOf(levelName);
        long timestamp = Long.parseLong(tokens[1]) * KaflogConstants.MILLISECONDS_IN_SECOND;
        return new LogMessage(-1, level, timestamp, tokens[2], tokens[3], tokens[4]);
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


    /**
     * List of facilities exported from unix documentation
     */
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


    /**
     * List of severity levels exported from unix documentation
     */
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