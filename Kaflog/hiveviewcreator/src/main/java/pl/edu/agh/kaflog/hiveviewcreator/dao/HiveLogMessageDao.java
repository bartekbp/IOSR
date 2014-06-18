package pl.edu.agh.kaflog.hiveviewcreator.dao;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import pl.edu.agh.kaflog.common.KaflogConstants;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Random;

/**
 * Hive dao implementation that handles {@link pl.edu.agh.kaflog.common.LogMessage} objects
 */
public class HiveLogMessageDao extends AbstractHiveDao {
    public static final String COL_NAME = "col_name";
    public static final String DATA_TYPE = "data_type";
    public static final String COMMENT = "comment";
    public static final String SEVERITY = "severity";
    public static final String TIME = "time";
    public static final String HOSTNAME = "hostname";
    public static final String SOURCE = "source";
    public static final String MESSAGE = "message";
    private final String hiveTable = KaflogProperties.getProperty("kaflog.hive.table");

    public HiveLogMessageDao() throws SQLException {

    }

    /**
     * Drops hive table configured in kaflog properties (kaflog.hive.table)
     * @return this
     * @throws SQLException
     */
    public HiveLogMessageDao dropTable() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeUpdate("drop table " + hiveTable);
            }
        });
        return this;
    }

    /*
        Ensures table existence with given delimiter
     */
    public HiveLogMessageDao createTableIfNotExistsWithFieldsDelimiter(final char delimiter) throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeUpdate("create table IF NOT EXISTS " + hiveTable +
                        " (severity string, time bigint, hostname string, source string, message string) " +
                        "ROW FORMAT DELIMITED  FIELDS TERMINATED BY '" + delimiter + "'");
            }
        });
        return this;
    }

    /**
     * Returns description of {@link pl.edu.agh.kaflog.common.LogMessage} data table
     * @return Description in form of list of lines
     * @throws SQLException
     */
    public List<String> describeTable() throws SQLException {
        return withResultSet(new CallableResultSet<List<String>>() {
            @Override
            public String query() {
                return "describe " + hiveTable;
            }

            @Override
            public List<String> call(ResultSet resultSet) throws SQLException {
                RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(resultSet);
                return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, java.lang.String>() {
                    @Override
                    public java.lang.String apply(DynaBean bean) {
                        return String.format("col_name=%s data_type=%s, comment=%s",
                                bean.get(COL_NAME),
                                bean.get(DATA_TYPE),
                                bean.get(COMMENT));
                    }
                });
            }
        });
    }


    /**
     * Load data from hdfs file to {@link pl.edu.agh.kaflog.common.LogMessage} table
     * @param path hdfs file path
     * @return this
     * @throws SQLException
     */
    public HiveLogMessageDao loadHdfs(final Path path) throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeUpdate("load data inpath '" + path + "' into table " + hiveTable);
            }
        });

        return this;
    }

    /**
     * Return all {@link pl.edu.agh.kaflog.common.LogMessage} from table
     * @return list of {@link pl.edu.agh.kaflog.common.LogMessage} stored in table
     * @throws SQLException
     */
    public List<LogMessage> selectAll() throws SQLException {
        return withResultSet(new CallableResultSet<List<LogMessage>>() {
            @Override
            public String query() {
                return "select * from " + hiveTable + " where `time` is not null";
            }

            @Override
            public List<LogMessage> call(ResultSet resultSet) throws SQLException {
                RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(resultSet);
                return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, LogMessage>() {
                    @Override
                    public LogMessage apply(DynaBean dynaBean) {
                        return LogMessage.fromHive(Joiner.on(KaflogConstants.SEPARATOR).join(
                                ObjectUtils.defaultIfNull(dynaBean.get(SEVERITY), LogMessage.LEVEL_STRING[LogMessage.LEVEL_STRING.length - 1]),
                                ObjectUtils.defaultIfNull(dynaBean.get(TIME), ""),
                                ObjectUtils.defaultIfNull(dynaBean.get(HOSTNAME), ""),
                                ObjectUtils.defaultIfNull(dynaBean.get(SOURCE), ""),
                                ObjectUtils.defaultIfNull(dynaBean.get(MESSAGE), "")));
                    }
                });
            }
        });
    }
}
