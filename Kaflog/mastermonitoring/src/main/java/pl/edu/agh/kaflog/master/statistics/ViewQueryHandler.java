package pl.edu.agh.kaflog.master.statistics;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ViewQueryHandler {
    private final ImpalaHBaseDao impalaHBaseDao;

    public ViewQueryHandler() throws SQLException {
        impalaHBaseDao = new ImpalaHBaseDao();
    }

    public Object createView(DateTime fromDate, DateTime toDate) throws SQLException {
      /*  List<Pair<String, Long>> hostData = Lists.newArrayList();
        List<Pair<String, Long>> severityData = Lists.newArrayList();

        for (Map.Entry<String, Long> entry : impalaHBaseDao.getHostResults(fromDate, toDate).entrySet()) {
            hostData.add(new Pair<String,Long>(entry.getKey(), entry.getValue()));
        }

        long all = 0;
        for (Map.Entry<String, Long> entry : impalaHBaseDao.getSeverityResults(fromDate, toDate).entrySet()) {
            severityData.add(new Pair<String,Long>(entry.getKey(), entry.getValue()));
            all += entry.getValue();
        }
        Report report = new Report();
        report.setHostData(hostData);
        report.setSeverityData(severityData);
        report.setAll(all);*/

        return Report.fake();

    }

}
