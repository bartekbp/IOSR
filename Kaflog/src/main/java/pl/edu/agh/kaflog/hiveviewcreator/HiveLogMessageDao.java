package pl.edu.agh.kaflog.hiveviewcreator;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.LogMessageSerializer;
import pl.edu.agh.kaflog.utils.CloseableUtils;
import pl.edu.agh.kaflog.utils.KaflogProperties;

import java.sql.*;
import java.util.List;

public class HiveLogMessageDao implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(HiveLogMessageDao.class);
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private final String jdbcUrl = KaflogProperties.getProperty("kaflog.hive.jdbc.url");
    private final String hiveTable = KaflogProperties.getProperty("kaflog.hive.table");

    static {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            LOG.error("", e);
        }
    }

    private final Connection connection;

    public HiveLogMessageDao() throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl, "vagrant", "");
    }

    public HiveLogMessageDao dropTable() throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate("drop table " + hiveTable);
        }

        return this;
    }

    public HiveLogMessageDao createTableIfNotExistsWithFieldsDelimiter(char delimiter) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate("create table IF NOT EXISTS " + hiveTable +
                    " (severity string, `date` string, time string, hostname string, source string, message string) " +
                    "ROW FORMAT DELIMITED  FIELDS TERMINATED BY '" + delimiter + "'");
        }


        return this;
    }

    public List<String> listTables() throws SQLException {
        try(Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery("show tables '*'")) {
            RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(res);
            return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, String>() {
                @Override
                public String apply(DynaBean bean) {
                    return (String) bean.get("tab_name");
                }
            });
        }
    }

    public List<String> describeTable() throws SQLException {
        try(Statement statement = connection.createStatement();
            ResultSet res = statement.executeQuery("describe " + hiveTable)){
            RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(res);
            return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, java.lang.String>() {
                @Override
                public java.lang.String apply(DynaBean bean) {
                    return String.format("col_name=%s data_type=%s, comment=%s",
                                            bean.get("col_name"),
                                            bean.get("data_type"),
                                            bean.get("comment"));
                }
            });
        }
    }

    public HiveLogMessageDao load(Path path) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.execute("load data local inpath '" + path + "' into table " + hiveTable);
            return this;
        }
    }

    public HiveLogMessageDao loadHdfs(Path path) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate("load data inpath '" + path + "' into table " + hiveTable);
            return this;
        }
    }

    public List<LogMessage> selectAll() throws SQLException {
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + hiveTable + " where `date` is not null")){
            RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(resultSet);
            return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, LogMessage>() {
                @Override
                public LogMessage apply(DynaBean dynaBean) {
                    return LogMessage.fromRawString(Joiner.on('\07').join(ObjectUtils.defaultIfNull(dynaBean.get("severity"), LogMessageSerializer.LEVEL_STRING[LogMessageSerializer.LEVEL_STRING.length - 1]),
                            ObjectUtils.defaultIfNull(dynaBean.get("date"), ""),
                            ObjectUtils.defaultIfNull(dynaBean.get("time"), ""),
                            ObjectUtils.defaultIfNull(dynaBean.get("hostname"), ""),
                            ObjectUtils.defaultIfNull(dynaBean.get("source"), ""),
                            ObjectUtils.defaultIfNull(dynaBean.get("message"), "")));
                }
            });
        }
    }

    @Override
    public void close()  {
        CloseableUtils.close(connection);
    }

}
