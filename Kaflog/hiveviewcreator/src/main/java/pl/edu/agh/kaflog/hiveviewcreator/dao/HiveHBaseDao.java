package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class HiveHBaseDao extends AbstractHiveDao {
    private long creationTimeInSec;
    public HiveHBaseDao() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        creationTimeInSec = calendar.getTimeInMillis() / 1000;
    }

    public void createHostPerSeverityPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_host_severity_per_time(key string, count bigint) " +
                        "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count#b\") " +
                        "TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_severity_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {
            @Override
            public String query() {
                return "insert OVERWRITE table hdp_host_severity_per_time select CONCAT(cast((cast(time / 3600 as BIGINT) * 3600) as string), rpad(hostname, 20, '0'), rpad(severity, 10, '0')), " +
                        "count(*) from kaflogdata where time < ? " +
                        "group by cast(time / 3600 as BIGINT), hostname, severity";
            }

            @Override
            public Object call(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, creationTimeInSec);
                return preparedStatement.execute();
            }

        });
    }

    public void ensureStromTablesAvailability() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute(
                        "create external table if not exists hbase_srm_host_severity_per_minute(key string, count bigint)\n" +
                                "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'\n" +
                                "WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count#b\")\n" +
                                "TBLPROPERTIES (\"hbase.table.name\" = \"srm_host_severity_per_minute\")");
            }
        });
    }


}
