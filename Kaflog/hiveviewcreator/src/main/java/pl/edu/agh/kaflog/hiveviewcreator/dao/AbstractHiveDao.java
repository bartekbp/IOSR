package pl.edu.agh.kaflog.hiveviewcreator.dao;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.utils.CloseableUtils;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.io.Closeable;
import java.sql.*;
import java.util.List;


/**
 * Encapsulates hive queries
 */
public abstract class AbstractHiveDao implements Closeable {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractHiveDao.class);
    public static final String TAB_NAME = "tab_name";
    private static String driverName = KaflogProperties.getProperty("kaflog.hive.driver.fullQualifiedName");
    private static final String jdbcUrl = KaflogProperties.getProperty("kaflog.hive.jdbc.url");
    private final Connection connection;

    static {
        try {
            //Ensures that driver is loaded by class loader
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            LOG.error("", e);
        }
    }

    /**
     * Connects to hive using default jdbsUrl
     * @throws SQLException
     */
    public AbstractHiveDao() throws SQLException {
       this(jdbcUrl);
    }

    /**
     * Connects to hive using provided jdbcUrl
     * @param jdbcUrl
     * @throws SQLException
     */
    public AbstractHiveDao(String jdbcUrl)throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl,
                KaflogProperties.getProperty("kaflog.hive.user"),
                KaflogProperties.getProperty("kaflog.hive.pass"));
    }

    /**
     * Executes hive statement and return its outcomes.
     * @param callableStatement state to execute
     * @param <T> type of outcomes
     * @return sql execution outcome
     * @throws SQLException
     */
    protected <T> T withStatement(CallablePreparedStatement<T> callableStatement) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(callableStatement.query());
            return callableStatement.call(statement);
        } finally {
            CloseableUtils.close(statement);
        }
    }

    /**
     * Executes hive statement and return its outcomes.
     * @param callableStatement state to execute
     * @param <T> type of outcomes
     * @return sql execution outcome
     * @throws SQLException
     */
    protected <T> T withStatement(CallableStatement<T> callableStatement) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return callableStatement.call(statement);
        } finally {
            CloseableUtils.close(statement);
        }
    }

    /**
     * Executes hive statement and return its outcomes.
     * @param callableResultSet state to execute
     * @param <T> type of outcomes
     * @return sql execution outcome
     * @throws SQLException
     */
    protected <T> T withResultSet(CallableResultSet<T> callableResultSet) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(callableResultSet.query());
            return callableResultSet.call(resultSet);
        } finally {
            CloseableUtils.close(resultSet);
            CloseableUtils.close(statement);
        }
    }

    /**
     * List names of all available tables in hive
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unused")
    public final List<String> listTables() throws SQLException {
        return withResultSet(new CallableResultSet<List<String>>() {
            @Override
            public String query() {
                return "show tables '*'";
            }

            @Override
            public List<String> call(ResultSet resultSet) throws SQLException {
                RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(resultSet);
                return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, String>() {
                    @Override
                    public String apply(DynaBean bean) {
                        return (String) bean.get(TAB_NAME);
                    }
                });
            }
        });}

    /**
     * Close connection to hive
     */
    @Override
    public void close() {
        CloseableUtils.close(connection);
    }
}
