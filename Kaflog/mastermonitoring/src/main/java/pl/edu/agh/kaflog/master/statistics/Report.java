package pl.edu.agh.kaflog.master.statistics;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {
    private Map<String, Map<String, Long>> hostSeverityData;
    private List<Pair<String, Long>> hostData;
    private List<Pair<String, Long>> severityData;
    private long all;

    static Report fake() {
        Report result = new Report();
        result.severityData = new ArrayList<Pair<String, Long>>();
        result.hostData = new ArrayList<Pair<String, Long>>();
        result.hostSeverityData = new HashMap<String, Map<String, Long>>();

        result.severityData.add(new Pair<String, Long>("DEBUG", 521L));
        result.severityData.add(new Pair<String, Long>("INFO", 1029L));
        result.severityData.add(new Pair<String, Long>("NOTICE", 7L));
        result.severityData.add(new Pair<String, Long>("WARN", 154L));
        result.severityData.add(new Pair<String, Long>("ERROR", 45L));
        result.severityData.add(new Pair<String, Long>("CRITICAL", 1L));
        result.severityData.add(new Pair<String, Long>("ALERT", 0L));
        result.severityData.add(new Pair<String, Long>("EMERGENCY", 0L));

        result.hostData.add(new Pair<String, Long>("192.168.0.12", 889L));
        result.hostData.add(new Pair<String, Long>("192.168.0.17", 868L));

        result.all = 1757L;

        Map<String, Long> h1Map = Maps.newHashMap();
        h1Map.put("DEBUG", 252L);
        h1Map.put("INFO", 573L);
        h1Map.put("NOTICE", 7L);
        h1Map.put("WARN", 52L);
        h1Map.put("ERROR", 4L);
        h1Map.put("CRITICAL", 1L);
        h1Map.put("ALERT", 0L);
        h1Map.put("EMERGENCY", 0L);

        Map<String, Long> h2Map = Maps.newHashMap();
        h2Map.put("DEBUG", 269L);
        h2Map.put("INFO", 456L);
        h2Map.put("NOTICE", 0L);
        h2Map.put("WARN", 102L);
        h2Map.put("ERROR", 41L);
        h2Map.put("CRITICAL", 0L);
        h2Map.put("ALERT", 0L);
        h2Map.put("EMERGENCY", 0L);

        result.hostSeverityData = Maps.newHashMap();
        result.hostSeverityData.put("192.168.0.12", h1Map);
        result.hostSeverityData.put("192.168.0.17", h2Map);

        return result;
    }

    public Map<String, Map<String, Long>> getHostSeverityData() {
        return hostSeverityData;
    }

    public void setHostSeverityData(Map<String, Map<String, Long>> hostSeverityData) {
        this.hostSeverityData = hostSeverityData;
    }

    public List<Pair<String, Long>> getHostData() {
        return hostData;
    }

    public void setHostData(List<Pair<String, Long>> hostData) {
        this.hostData = hostData;
    }

    public List<Pair<String, Long>> getSeverityData() {
        return severityData;
    }

    public void setSeverityData(List<Pair<String, Long>> severityData) {
        this.severityData = severityData;
    }

    public long getAll() {
        return all;
    }

    public void setAll(long all) {
        this.all = all;
    }
}