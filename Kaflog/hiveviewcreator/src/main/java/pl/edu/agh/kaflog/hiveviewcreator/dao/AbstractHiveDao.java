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
import java.util.Collection;
import java.util.List;

public abstract class AbstractHiveDao implements Closeable {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractHiveDao.class);
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static final String jdbcUrl = KaflogProperties.getProperty("kaflog.hive.jdbc.url");
    private final Connection connection;

    static {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            LOG.error("", e);
        }
    }

    public AbstractHiveDao() throws SQLException {
       this(jdbcUrl);
    }

    public AbstractHiveDao(String jdbcUrl)throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl, "vagrant", "");
    }

    protected <T> T withStatement(CallablePreparedStatement<T> callableStatement) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(callableStatement.query());
            return callableStatement.call(statement);
        } finally {
            CloseableUtils.close(statement);
        }
    }

    protected <T> T withStatement(CallableStatement<T> callableStatement) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return callableStatement.call(statement);
        } finally {
            CloseableUtils.close(statement);
        }
    }

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
                        return (String) bean.get("tab_name");
                    }
                });
            }
        });}

    @Override
    public void close() {
        CloseableUtils.close(connection);
    }
}
