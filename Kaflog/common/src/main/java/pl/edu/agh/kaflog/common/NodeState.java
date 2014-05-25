package pl.edu.agh.kaflog.common;

import java.io.Serializable;

public class NodeState implements Serializable {
    private String ip;
    private String hostname;
    private long startTime;

    private int totalLogs;
    private int logsInLastDay;
    private int logsInLastHour;
    private int logsInLastMinute;

    public NodeState(String ip, String hostname, long startTime, int totalLogs, int logsInLastDay, int logsInLastHour, int logsInLastMinute) {
        this.ip = ip;
        this.hostname = hostname;
        this.startTime = startTime;
        this.totalLogs = totalLogs;
        this.logsInLastDay = logsInLastDay;
        this.logsInLastHour = logsInLastHour;
        this.logsInLastMinute = logsInLastMinute;
    }

    public String getIp() {
        return ip;
    }

    public String getHostname() {
        return hostname;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTotalLogs() {
        return totalLogs;
    }

    public int getLogsInLastDay() {
        return logsInLastDay;
    }

    public int getLogsInLastHour() {
        return logsInLastHour;
    }

    public int getLogsInLastMinute() {
        return logsInLastMinute;
    }

    @Override
    public String toString() {
        return "NodeState{" +
                "ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", startTime=" + startTime +
                ", totalLogs=" + totalLogs +
                ", logsInLastDay=" + logsInLastDay +
                ", logsInLastHour=" + logsInLastHour +
                ", logsInLastMinute=" + logsInLastMinute +
                '}';
    }
}
