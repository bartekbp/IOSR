package pl.edu.agh.kaflog.common;


import java.io.*;

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
        return String.format("[%s] %s %s @%s %s: %s",
                LogMessageSerializer.LEVEL_STRING[severity],
                date,
                time,
                hostname,
                source,
                message);
    }

    private String toString(String sep) {
        return String.format("[%s]" + sep + "%s" + sep + "%s" + sep + "%s@%s" + sep + "%s",
                LogMessageSerializer.LEVEL_STRING[severity],
                date,
                time,
                hostname,
                source,
                message);
    }

    public String toRaw() {
        return toString(";");
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