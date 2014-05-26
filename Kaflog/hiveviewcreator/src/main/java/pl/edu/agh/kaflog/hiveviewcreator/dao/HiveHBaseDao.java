package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.SQLException;
import java.sql.Statement;

public class HiveHBaseDao extends AbstractHiveDao {
    public HiveHBaseDao() throws SQLException {
    }

    public void createSeverityPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_severity_per_time(severity string, 24h bigint, total bigint) STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key,f:24h, f:total\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_severity_per_time\")");
            }
        });

        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeQuery("insert OVERWRITE table hdp_severity_per_time select a.severity, b.res 24h, c.res total from kaflogdata a join (select severity, count(*) res from kaflogdata where time >= (UNIX_TIMESTAMP() - 24 * 60 * 60) group by severity) b on a.severity = b.severity join (select severity, count(*) res from kaflogdata where time >= (UNIX_TIMESTAMP() - 24 * 60 * 60 * 365) group by severity) c on c.severity = a.severity");
            }
        });
    }

    public void createHostPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_host_per_time(hostname string, 24h bigint, total bigint) STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key,f:24h, f:total\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_per_time\")");
            }
        });

        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeQuery("insert OVERWRITE table hdp_host_per_time select a.hostname, b.res 24h, c.res total from kaflogdata a join (select hostname, count(*) res from kaflogdata where time >= (UNIX_TIMESTAMP() - 24 * 60 * 60) group by hostname) b on a.hostname = b.hostname join (select hostname, count(*) res from kaflogdata where time >= (UNIX_TIMESTAMP() - 24 * 60 * 60 * 365) group by hostname) c on c.hostname = a.hostname");
            }
        });
    }

    public void createHostPerSeverityPerTimeView() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("CREATE TABLE if not exists hdp_host_severity_per_time(key struct<host:string,severity:string>, 24h bigint, total bigint) ROW FORMAT DELIMITED COLLECTION ITEMS TERMINATED BY '\7' STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key, f:24h, f:total\") TBLPROPERTIES (\"hbase.table.name\" = \"hdp_host_severity_per_time\")");
            }
        });

        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeQuery("insert OVERWRITE table hdp_host_severity_per_time select named_struct('host', a.hostname, 'severity', a.severity), b.res 24h, c.res total from kaflogdata a join (select hostname, severity, count(*) res from kaflogdata where time >= (UNIX_TIMESTAMP() - 24 * 60 * 60) group by hostname, severity with cube) b on a.hostname = b.hostname and a.severity = b.severity join (select hostname, severity, count(*) res from kaflogdata where time >= (UNIX_TIMESTAMP() - 24 * 60 * 60 * 365) group by hostname, severity with cube) c on c.hostname = a.hostname and c.severity = a.severity");
            }
        });
    }

}
