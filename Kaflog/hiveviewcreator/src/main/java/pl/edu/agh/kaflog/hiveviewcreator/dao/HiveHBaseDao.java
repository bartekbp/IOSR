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

    public void createSeverityPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_severity_per_time(key struct<severity:string,year:int,month:int,day:int,hour:int>, count bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_severity_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {

            public String query() {
                return "insert OVERWRITE table hdp_severity_per_time select named_struct('severity', severity, 'year', year(FROM_UNIXTIME(time)), 'month', month(FROM_UNIXTIME(time)), 'day', day(FROM_UNIXTIME(time)), 'hour', hour(FROM_UNIXTIME(time))), count(*) from kaflogdata where time < ? group by severity, year(FROM_UNIXTIME(time)), month(FROM_UNIXTIME(time)), day(FROM_UNIXTIME(time)), hour(FROM_UNIXTIME(time))";
            }

            @Override
            public Object call(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, creationTimeInSec);
                return preparedStatement.executeQuery();
            }
        });
    }

    public void createHostPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_host_per_time(key struct<host:string,year:int,month:int,day:int,hour:int>, count bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {

            @Override
            public String query() {
                return "insert OVERWRITE table hdp_host_per_time select named_struct('host', hostname, 'year', year(FROM_UNIXTIME(time)), 'month', month(FROM_UNIXTIME(time)), 'day', day(FROM_UNIXTIME(time)), 'hour', hour(FROM_UNIXTIME(time))), count(*) from kaflogdata where time < ? group by hostname, year(FROM_UNIXTIME(time)), month(from_unixtime(time)), day(FROM_UNIXTIME(time)), hour(FROM_UNIXTIME(time))";
            }

            @Override
            public Object call(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, creationTimeInSec);
                return preparedStatement.execute();
            }
        });
    }

    public void createHostPerSeverityPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_host_severity_per_time(key struct<host:string,severity:string,year:int,month:int,day:int,hour:int>, count bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_severity_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {
            @Override
            public String query() {
                return "insert OVERWRITE table hdp_host_severity_per_time select named_struct('host', hostname, 'severity', severity, 'year', year(FROM_UNIXTIME(time)), 'month', month(FROM_UNIXTIME(time)), 'day', day(FROM_UNIXTIME(time)), 'hour', hour(FROM_UNIXTIME(time))), count(*) from kaflogdata where time < ? group by hostname, severity, year(FROM_UNIXTIME(time)), month(from_unixtime(time)), day(FROM_UNIXTIME(time)), hour(FROM_UNIXTIME(time))";
            }

            @Override
            public Object call(PreparedStatement preparedStatement) throws SQLException {
                preparedStatement.setLong(1, creationTimeInSec);
                return preparedStatement.execute();
            }

        });
    }

}
