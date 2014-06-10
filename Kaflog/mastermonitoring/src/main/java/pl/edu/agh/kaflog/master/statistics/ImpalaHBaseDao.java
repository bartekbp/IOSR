package pl.edu.agh.kaflog.master.statistics;

import org.apache.commons.beanutils.RowSetDynaClass;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import pl.edu.agh.kaflog.hiveviewcreator.dao.AbstractHiveDao;
import pl.edu.agh.kaflog.hiveviewcreator.dao.CallablePreparedStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImpalaHBaseDao extends AbstractHiveDao {


    public ImpalaHBaseDao() throws SQLException {
    }

    public void getHostResults(DateTime from, DateTime to) {
        DateTime hadoopFrom = from.withMinuteOfHour(00);
        DateTime hadoopTo = to.withMinuteOfHour(00);
        DateTime now = new DateTime();
        final DateTime stormFrom;
        final DateTime stormTo;

        Interval interval = new Interval(now, to);
        if (interval.toDuration().isShorterThan(Duration.standardHours(1L))) {
            stormFrom = to.withMinuteOfHour(00);
            stormTo = to;
        } else {
            stormFrom = now;
            stormTo = now;
        }
        try {
            if (stormFrom.isBefore(stormTo)) {
                RowSetDynaClass stormRows = new RowSetDynaClass(withStatement(new CallablePreparedStatement<ResultSet>() {

                    @Override
                    public String query() {
                        return "select * from hbase_srm_host_per_minute where key.hour= ? and key.minute< ?";
                    }

                    @Override
                    public ResultSet call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setInt(1, stormFrom.getHourOfDay());
                        preparedStatement.setInt(2, stormTo.getMinuteOfHour());
                        return preparedStatement.executeQuery();
                    }
                }));
            }
            if (hadoopFrom.isBefore(hadoopTo)) {
                RowSetDynaClass stormRows = new RowSetDynaClass(withStatement(new CallablePreparedStatement<ResultSet>() {

                    @Override
                    public String query() {

                        return  "select * from hbase_srm_host_per_minute where check_time(key, ?, ?)";
                    }

                    @Override
                    public ResultSet call(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, stormFrom.toInstant().getMillis());
                        preparedStatement.setLong(2, stormTo.toInstant().getMillis());
                        return preparedStatement.executeQuery();
                    }
                }));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
