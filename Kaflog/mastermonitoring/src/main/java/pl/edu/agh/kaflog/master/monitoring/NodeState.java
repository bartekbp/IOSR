package pl.edu.agh.kaflog.master.monitoring;

/**
 * Created by lopiola on 22.05.14.
 */
public class NodeState implements Comparable<NodeState> {
    private String ip;
    private String hostname;

    private long lastHeartbeat;
    private long uptime;

    private int totalLogs;
    private int logsInlastDay;
    private int logsInLastHour;
    private int logsInLastMinute;

    public NodeState(String ip, String hostname, long lastHeartbeat, long uptime, int totalLogs, int logsInlastDay, int logsInLastHour, int logsInLastMinute) {
        this.ip = ip;
        this.hostname = hostname;
        this.lastHeartbeat = lastHeartbeat;
        this.uptime = uptime;
        this.totalLogs = totalLogs;
        this.logsInlastDay = logsInlastDay;
        this.logsInLastHour = logsInLastHour;
        this.logsInLastMinute = logsInLastMinute;
    }

    @Override
    public int compareTo(NodeState o) {
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

    public int getLogsInlastDay() {
        return logsInlastDay;
    }

    public int getLogsInLastHour() {
        return logsInLastHour;
    }

    public int getLogsInLastMinute() {
        return logsInLastMinute;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public void setTotalLogs(int totalLogs) {
        this.totalLogs = totalLogs;
    }

    public void setLogsInlastDay(int logsInlastDay) {
        this.logsInlastDay = logsInlastDay;
    }

    public void setLogsInLastHour(int logsInLastHour) {
        this.logsInLastHour = logsInLastHour;
    }

    public void setLogsInLastMinute(int logsInLastMinute) {
        this.logsInLastMinute = logsInLastMinute;
    }
}
