package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @param <T> Type of outcome of execution of statement
 */
public interface CallablePreparedStatement<T> {
    /**
     * @return query as SQL string
     */
    public String query();

    /**
     * Executes query
     * @param preparedStatement query to execute
     * @return
     * @throws SQLException
     */
    public T call(PreparedStatement preparedStatement) throws SQLException;
}
