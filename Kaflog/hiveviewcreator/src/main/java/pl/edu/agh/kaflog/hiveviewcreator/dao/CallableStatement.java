package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.SQLException;
import java.sql.Statement;

public interface CallableStatement<T> {
    public T call(Statement statement) throws SQLException;
}
