package pl.edu.agh.kaflog.master.statistics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import pl.edu.agh.kaflog.common.LogMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates all aggregated data
 * Standard Java bean
 */
public class Report {
    private static final List<String> severitiesList =
            ImmutableList.<String>builder().add(LogMessage.LEVEL_STRING).build();

    private DateTime startDate, endDate;
    private Map<String, Map<String, Long>> hostSeverityData;
    private List<Pair<String, Long>> hostData;
    private List<Pair<String, Long>> severityData;
    private long all;

    /**
     * Creates fake report for testing purposes
     * @return fake report
     */
    static Report fake(DateTime startDate, DateTime endDate) {

        Report result = new Report();
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        result.severityData = new ArrayList<Pair<String, Long>>();
        result.hostData = new ArrayList<Pair<String, Long>>();
        result.hostSeverityData = new HashMap<String, Map<String, Long>>();

        result.severityData.add(new Pair<String, Long>("debug", 521L));
        result.severityData.add(new Pair<String, Long>("info", 1029L));
        result.severityData.add(new Pair<String, Long>("notice", 7L));
        result.severityData.add(new Pair<String, Long>("warn", 154L));
        result.severityData.add(new Pair<String, Long>("error", 45L));
        result.severityData.add(new Pair<String, Long>("critical", 1L));
        result.severityData.add(new Pair<String, Long>("alert", 0L));
        result.severityData.add(new Pair<String, Long>("emergency", 0L));

        result.hostData.add(new Pair<String, Long>("192.168.0.12", 889L));
        result.hostData.add(new Pair<String, Long>("192.168.0.17", 868L));

        result.all = 1757L;

        Map<String, Long> h1Map = Maps.newHashMap();
        h1Map.put("debug", 252L);
        h1Map.put("info", 573L);
        h1Map.put("notice", 7L);
        h1Map.put("warn", 52L);
        h1Map.put("error", 4L);
        h1Map.put("critical", 1L);
        h1Map.put("alert", 0L);
        h1Map.put("emergency", 0L);

        Map<String, Long> h2Map = Maps.newHashMap();
        h2Map.put("debug", 269L);
        h2Map.put("info", 456L);
        h2Map.put("notice", 0L);
        h2Map.put("warn", 102L);
        h2Map.put("error", 41L);
        h2Map.put("critical", 0L);
        h2Map.put("alert", 0L);
        h2Map.put("emergency", 0L);

        result.hostSeverityData = Maps.newHashMap();
        result.hostSeverityData.put("192.168.0.12", h1Map);
        result.hostSeverityData.put("192.168.0.17", h2Map);

        return result;
    }

    /**
     * @return Map of hosts' names to severity to number of logs map (Map<HostName, Map<SeverityLevel, NumberOfLogs>>)
     */
    public Map<String, Map<String, Long>> getHostSeverityData() {
        return hostSeverityData;
    }

    /**
     * @param hostSeverityData Map of hosts' names to severity to number of logs map (Map<HostName, Map<SeverityLevel, NumberOfLogs>>)
     */
    public void setHostSeverityData(Map<String, Map<String, Long>> hostSeverityData) {
        this.hostSeverityData = hostSeverityData;
    }

    /**
     * @return List of pairs - hostname to number of logs
     */
    public List<Pair<String, Long>> getHostData() {
        return hostData;
    }

    /**
     * List of pairs - hostname to number of logs
     * @param hostData
     */
    public void setHostData(List<Pair<String, Long>> hostData) {
        this.hostData = hostData;
    }

    /**
     * @return List of pairs - severity level to number of logs
     */
    public List<Pair<String, Long>> getSeverityData() {
        return severityData;
    }

    /**
     * @param severityData List of pairs - severity level to number of logs
     */
    public void setSeverityData(List<Pair<String, Long>> severityData) {
        this.severityData = severityData;
    }

    /**
     *
     * @return total number of all logs
     */
    public long getAll() {
        return all;
    }

    /**
     *
     * @param all total number of all logs
     */
    public void setAll(long all) {
        this.all = all;
    }

    /**
     *
     * @return list of all possible severities
     */
    public static List<String> getSeveritiesList() {
        return severitiesList;
    }

    public DateTime getStartDate() {

        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }
}