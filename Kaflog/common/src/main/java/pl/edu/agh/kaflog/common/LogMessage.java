package pl.edu.agh.kaflog.common;


import java.io.*;
import java.util.Arrays;

public class LogMessage implements Serializable {
    private int facility;
    private int severity;
    private String date;
    private String time;
    private String hostname;
    private String source;
    private String message;

    public LogMessage(int facility, int severity, String date, String time, String hostname, String source, String message) {
        this.facility = facility;
        this.severity = severity;
        this.date = date;
        this.time = time;
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
        return String.format("[%s]" + sep + "%s" + sep + "%s" + sep + "%s" + sep + "%s" + sep + "%s",
                LogMessageSerializer.LEVEL_STRING[severity],
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
        int level = Arrays.asList(LogMessageSerializer.LEVEL_STRING).indexOf(levelName);
        return new LogMessage(-1, level, tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
    }

    public int getFacility() {
        return facility;
    }

    public int getSeverity() {
        return severity;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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
}