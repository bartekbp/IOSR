package pl.edu.agh.kaflog.master.monitoring;

import pl.edu.agh.kaflog.common.NodeState;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;

/**
 * Created by lopiola on 22.05.14.
 */
public class NodeStateSummary implements Comparable<NodeStateSummary> {
    private String hostname;
    private String ip;

    private long lastHeartbeat;
    private long startTime;

    private int totalLogs;
    private int logsInLastDay;
    private int logsInLastHour;
    private int logsInLastMinute;

    public NodeStateSummary(String hostname, String ip) {
        this.hostname = hostname;
        this.ip = ip;
    }

    public void update(NodeState nodeState) {
        lastHeartbeat = KaflogDateUtils.getCurrentTime();
        startTime = nodeState.getStartTime();
        totalLogs = nodeState.getTotalLogs();
        logsInLastDay = nodeState.getLogsInLastDay();
        logsInLastHour = nodeState.getLogsInLastHour();
        logsInLastMinute = nodeState.getLogsInLastMinute();
    }

    @Override
    public int compareTo(NodeStateSummary o) {
        return hostname.compareTo(o.hostname);
    }

    public String getIp() {
        return ip;
    }

    public String getHostname() {
        return hostname;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
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

    public long getUptime() {
        return KaflogDateUtils.getCurrentTime() - startTime;
    }

    @Override
    public String toString() {
        return "NodeStateSummary{" +
                "ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", lastHeartbeat=" + lastHeartbeat +
                ", startTime=" + startTime +
                ", totalLogs=" + totalLogs +
                ", logsInLastDay=" + logsInLastDay +
                ", logsInLastHour=" + logsInLastHour +
                ", logsInLastMinute=" + logsInLastMinute +
                '}';
    }
}
