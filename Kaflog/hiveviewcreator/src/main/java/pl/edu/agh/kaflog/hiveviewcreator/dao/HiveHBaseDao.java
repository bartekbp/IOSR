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
                return statement.execute("CREATE TABLE if not exists hdp_severity_per_time(key struct<severity:string,time:bigint>, count bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_severity_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {

            public String query() {
                return "insert OVERWRITE table hdp_severity_per_time select named_struct('severity', severity, 'time', cast(time / 3600 as BIGINT) * 3600), count(*) from kaflogdata where time < ? group by severity, cast(time / 3600 as BIGINT)";
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
                return statement.execute("CREATE TABLE if not exists hdp_host_per_time(key struct<host:string,time:bigint>, count bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {

            @Override
            public String query() {
                return "insert OVERWRITE table hdp_host_per_time select named_struct('host', hostname, 'time',  cast(time / 3600 as BIGINT) * 3600), count(*) from kaflogdata where time < ? group by hostname, cast(time / 3600 as BIGINT)";
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
                return statement.execute("CREATE TABLE if not exists hdp_host_severity_per_time(key struct<host:string,severity:string,time:bigint>, count bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_severity_per_time\")");
            }
        });

        withStatement(new CallablePreparedStatement<Object>() {
            @Override
            public String query() {
                return "insert OVERWRITE table hdp_host_severity_per_time select named_struct('host', hostname, 'severity', severity, 'time', cast(time / 3600 as BIGINT) * 3600), count(*) from kaflogdata where time < ? group by hostname, severity, cast(time / 3600 as BIGINT)";
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
                        "create external table if not exists hbase_srm_host_per_minute(key struct<host:string,time:bigint>, value int)\n" +
                                "ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7'\n" +
                                "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'\n" +
                                "WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\")\n" +
                                "TBLPROPERTIES (\"hbase.table.name\" = \"srm_host_per_minute\")");
            }
        });
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute(
                        "create external table if not exists hbase_srm_host_severity_per_minute(key struct<host:string,severity:string,time:bigint>, value int)\n" +
                                "ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7'\n" +
                                "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'\n" +
                                "WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\")\n" +
                                "TBLPROPERTIES (\"hbase.table.name\" = \"srm_host_severity_per_minute\")");
            }
        });
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute(
                        "create external table if not exists hbase_srm_severity_per_minute(key struct<severity:string,time:bigint>, value int)\n" +
                                "ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7'\n" +
                                "STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'\n" +
                                "WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:count\")\n" +
                                "TBLPROPERTIES (\"hbase.table.name\" = \"srm_severity_per_minute\")");
            }
        });
    }


}
