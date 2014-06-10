package pl.edu.agh.kaflog.master.statistics;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import pl.edu.agh.kaflog.hiveviewcreator.dao.AbstractHiveDao;
import pl.edu.agh.kaflog.hiveviewcreator.dao.CallablePreparedStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImpalaHBaseDao extends AbstractHiveDao {

    public Map<String, Map<String,Long>> getHostSeverityResults(DateTime from, DateTime to) throws SQLException {
        final DateTime hadoopFrom = from.withMinuteOfHour(00);
        final DateTime hadoopTo = to.withMinuteOfHour(00);
        DateTime now = new DateTime();
        final DateTime stormFrom;
        final DateTime stormTo;

        Interval interval = new Interval(to, now);
        if (interval.toDuration().isShorterThan(Duration.standardHours(1L))) {
            stormFrom = to.withMinuteOfHour(00);
            stormTo = to;
        } else {
            stormFrom = now;
            stormTo = now;
        }

        Map<String, Map<String, Long>> hostToSevToLogNum = new HashMap<String, Map<String, Long>>();

        try {
            if (stormFrom.isBefore(stormTo)) {
                RowSetDynaClass stormRows = withStatement(new CallablePreparedStatement<RowSetDynaClass>() {

                    @Override
                    public String query() {
                        return "select key.host as host, key.severity as sev, sum(value) as s from hbase_srm_host_severity_per_minute where key.time > ? and key.time <= ? group by key.host, key.severity";
                    }

                    @Override
                    public RowSetDynaClass call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, stormFrom.getMillis() / 1000);
                        preparedStatement.setLong(2, stormTo.getMillis() / 1000);
                        return new RowSetDynaClass(preparedStatement.executeQuery());
                    }
                });
                for(Object rawRow : stormRows.getRows()) {
                    DynaBean row = (DynaBean) rawRow;
                    String host = (String) row.get("host");
                    String sev = (String) row.get("sev");
                    Long sum = (Long) row.get("s");
                    long currentSum = sum;
                    if(!hostToSevToLogNum.containsKey(host)) {
                        hostToSevToLogNum.put(host, new HashMap<String, Long>());
                    }

                    if(hostToSevToLogNum.get(host).containsKey(sev)) {
                        currentSum += hostToSevToLogNum.get(host).get(sev);
                    }

                    hostToSevToLogNum.get(host).put(sev, currentSum);
                }
            }
            if (hadoopFrom.isBefore(hadoopTo)) {
                RowSetDynaClass hadoopRows = withStatement(new CallablePreparedStatement<RowSetDynaClass>() {

                    @Override
                    public String query() {
                        return  "select key.host as host, key.severity as sev, sum(count) as s from hdp_host_severity_per_time where key.time > ? and key.time <= ? group by key.host, key.severity";
                    }

                    @Override
                    public RowSetDynaClass call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, hadoopFrom.getMillis() / 1000);
                        preparedStatement.setLong(2, hadoopTo.getMillis() / 1000);
                        return new RowSetDynaClass(preparedStatement.executeQuery());
                    }
                });
                for(Object rawRow : hadoopRows.getRows()) {
                    DynaBean row = (DynaBean) rawRow;
                    String host = (String) row.get("host");
                    String sev = (String) row.get("sev");
                    Long sum = (Long) row.get("s");
                    long currentSum = sum;
                    if(!hostToSevToLogNum.containsKey(host)) {
                        hostToSevToLogNum.put(host, new HashMap<String, Long>());
                    }

                    if(hostToSevToLogNum.get(host).containsKey(sev)) {
                        currentSum += hostToSevToLogNum.get(host).get(sev);
                    }

                    hostToSevToLogNum.get(host).put(sev, currentSum);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hostToSevToLogNum;
    }

    public ImpalaHBaseDao() throws SQLException {
    }

    public Map<String, Long> getSeverityResults(DateTime from, DateTime to) throws SQLException {
        final DateTime hadoopFrom = from.withMinuteOfHour(00);
        final DateTime hadoopTo = to.withMinuteOfHour(00);
        DateTime now = new DateTime();
        final DateTime stormFrom;
        final DateTime stormTo;

        Interval interval = new Interval(to, now);
        if (interval.toDuration().isShorterThan(Duration.standardHours(1L))) {
            stormFrom = to.withMinuteOfHour(00);
            stormTo = to;
        } else {
            stormFrom = now;
            stormTo = now;
        }

        Map<String, Long> severityToLogNum = new HashMap<String, Long>();

        try {
            if (stormFrom.isBefore(stormTo)) {
                RowSetDynaClass stormRows = withStatement(new CallablePreparedStatement<RowSetDynaClass>() {

                    @Override
                    public String query() {
                        return "select key.severity as sev, sum(value) as s from hbase_srm_severity_per_minute where key.time > ? and key.time <= ? group by key.severity";
                    }

                    @Override
                    public RowSetDynaClass call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, stormFrom.getMillis() / 1000);
                        preparedStatement.setLong(2, stormTo.getMillis() / 1000);
                        return new RowSetDynaClass(preparedStatement.executeQuery());
                    }
                });
                for(Object rawRow : stormRows.getRows()) {
                    DynaBean row = (DynaBean) rawRow;
                    severityToLogNum.put((String) row.get("sev"), (Long) row.get("s"));
                }
            }
            if (hadoopFrom.isBefore(hadoopTo)) {
                RowSetDynaClass hadoopRows = withStatement(new CallablePreparedStatement<RowSetDynaClass>() {

                    @Override
                    public String query() {
                        return  "select key.severity as sev, sum(count) as s from hdp_severity_per_time where key.time > ? and key.time <= ? group by key.severity";
                    }

                    @Override
                    public RowSetDynaClass call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, hadoopFrom.getMillis() / 1000);
                        preparedStatement.setLong(2, hadoopTo.getMillis() / 1000);
                        return new RowSetDynaClass(preparedStatement.executeQuery());
                    }
                });
                for(Object rawRow : hadoopRows.getRows()) {
                    DynaBean row = (DynaBean) rawRow;
                    long numOfStormResults = 0;
                    if(severityToLogNum.containsKey(row.get("sev"))) {
                        numOfStormResults = severityToLogNum.get(row.get("sev"));
                    }

                    severityToLogNum.put((String) row.get("sev"), numOfStormResults + (Long) row.get("s"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return severityToLogNum;
    }

    public Map<String, Long> getHostResults(DateTime from, DateTime to) throws SQLException {
        final DateTime hadoopFrom = from.withMinuteOfHour(00);
        final DateTime hadoopTo = to.withMinuteOfHour(00);
        DateTime now = new DateTime();
        final DateTime stormFrom;
        final DateTime stormTo;

        Interval interval = new Interval(to, now);
        if (interval.toDuration().isShorterThan(Duration.standardHours(1L))) {
            stormFrom = to.withMinuteOfHour(00);
            stormTo = to;
        } else {
            stormFrom = now;
            stormTo = now;
        }

        Map<String, Long> hostToLogNum = new HashMap<String, Long>();

        try {
            if (stormFrom.isBefore(stormTo)) {
                RowSetDynaClass stormRows = withStatement(new CallablePreparedStatement<RowSetDynaClass>() {

                    @Override
                    public String query() {
                        return "select key.host as host, sum(value) as s from hbase_srm_host_per_minute where key.time > ? and key.time <= ? group by key.host";
                    }

                    @Override
                    public RowSetDynaClass call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, stormFrom.getMillis() / 1000);
                        preparedStatement.setLong(2, stormTo.getMillis() / 1000);
                        return new RowSetDynaClass(preparedStatement.executeQuery());
                    }
                });
                for(Object rawRow : stormRows.getRows()) {
                    DynaBean row = (DynaBean) rawRow;
                    hostToLogNum.put((String) row.get("host"), (Long) row.get("s"));
                }
            }
            if (hadoopFrom.isBefore(hadoopTo)) {
                RowSetDynaClass hadoopRows = withStatement(new CallablePreparedStatement<RowSetDynaClass>() {

                    @Override
                    public String query() {
                        return  "select key.host as host, sum(count) as s from hdp_host_per_time where key.time > ? and key.time <= ? group by key.host";
                    }

                    @Override
                    public RowSetDynaClass call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, hadoopFrom.getMillis() / 1000);
                        preparedStatement.setLong(2, hadoopTo.getMillis() / 1000);
                        return new RowSetDynaClass(preparedStatement.executeQuery());
                    }
                });
                for(Object rawRow : hadoopRows.getRows()) {
                    DynaBean row = (DynaBean) rawRow;
                    long numOfStormResults = 0;
                    if(hostToLogNum.containsKey(row.get("host"))) {
                        numOfStormResults = hostToLogNum.get(row.get("host"));
                    }

                    hostToLogNum.put((String) row.get("host"), numOfStormResults + (Long) row.get("s"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hostToLogNum;
    }
}
