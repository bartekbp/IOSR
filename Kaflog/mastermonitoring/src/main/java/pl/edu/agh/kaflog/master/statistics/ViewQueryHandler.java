package pl.edu.agh.kaflog.master.statistics;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Component
public class ViewQueryHandler {
    @Autowired
    private ImpalaHBaseDao impalaHBaseDao;

    /**
     * Delegates query to ImpalaHBaseDao and converts query results into Report object that is
     * later used to create report view
     * @param fromDate - start report date
     * @param toDate - end report date
     * @return Report object
     * @throws SQLException
     */
    public Object createView(DateTime fromDate, DateTime toDate) throws SQLException {
        List<Pair<String, Long>> hostData = Lists.newArrayList();
        List<Pair<String, Long>> severityData = Lists.newArrayList();
        Map<String, Map<String, Long>> hostSeverityData = Maps.newHashMap();

        Map<String, Map<String, Long>> rawData = impalaHBaseDao.getHostSeverityResults(fromDate, toDate);
        Map<String, Long> tmpSeverityData = Maps.newHashMap();

        for (String severityName : Report.getSeveritiesList()) {
            tmpSeverityData.put(severityName, 0L);
        }

        long all = 0L;
        for (String hostName : rawData.keySet()) {
            long oneHostSum = 0L;
            Map<String, Long> oneHostSeverityData = Maps.newHashMap();
            Map<String, Long> rawHostData = rawData.get(hostName);

            for (String severityName: Report.getSeveritiesList()) {
                long severityValue = 0L;
                if (rawHostData.containsKey(severityName)) {
                    severityValue = Objects.firstNonNull(rawHostData.get(severityName), 0L);
                }
                tmpSeverityData.put(severityName, tmpSeverityData.get(severityName) + severityValue);
                oneHostSeverityData.put(severityName, severityValue);
                oneHostSum += severityValue;
            }

            hostSeverityData.put(hostName, oneHostSeverityData);
            hostData.add(new Pair<String, Long>(hostName, oneHostSum));
            all += oneHostSum;
        }

        for (String severityName : Report.getSeveritiesList()) {
            severityData.add(new Pair<String, Long>(severityName, tmpSeverityData.get(severityName)));
        }

        Report report = new Report();
        report.setHostData(hostData);
        report.setSeverityData(severityData);
        report.setHostSeverityData(hostSeverityData);
        report.setAll(all);
        report.setStartDate(fromDate);
        report.setEndDate(toDate);

        return report;
    }

}
