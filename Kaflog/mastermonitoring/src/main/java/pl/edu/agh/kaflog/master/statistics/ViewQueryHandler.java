package pl.edu.agh.kaflog.master.statistics;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
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

        for (Map.Entry<String, Long> entry : impalaHBaseDao.getHostResults(fromDate, toDate).entrySet()) {
            hostData.add(new Pair<String,Long>(Objects.firstNonNull(entry.getKey(),""), entry.getValue()));
        }

        long all = 0;
        for (Map.Entry<String, Long> entry : impalaHBaseDao.getSeverityResults(fromDate, toDate).entrySet()) {
            severityData.add(new Pair<String,Long>(entry.getKey(), entry.getValue()));
            all += entry.getValue();
        }

        Map<String, Map<String, Long>> hostSeverityData = impalaHBaseDao.getHostSeverityResults(fromDate, toDate);
        Report report = new Report();
        report.setHostData(hostData);
        report.setSeverityData(severityData);
        report.setHostSeverityData(hostSeverityData);
        report.setAll(all);

        return report;
      //  return Report.fake();
    }

}
