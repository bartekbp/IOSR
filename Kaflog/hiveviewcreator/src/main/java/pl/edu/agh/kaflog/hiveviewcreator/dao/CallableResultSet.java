package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface CallableResultSet<T> {
    public String query();
    public T call(ResultSet resultSet) throws SQLException;
}
