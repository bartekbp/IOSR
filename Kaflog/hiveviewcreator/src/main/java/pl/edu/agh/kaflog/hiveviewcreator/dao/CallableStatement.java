package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Encapsulates callable SQL query
 * @param <T> type of outcomes of execution of query
 */
public interface CallableStatement<T> {
    public T call(Statement statement) throws SQLException;
}
