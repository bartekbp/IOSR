package pl.edu.agh.kaflog.hivedao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CallableResultSet<T> {
    public String query();
    public T call(ResultSet resultSet) throws SQLException;
}
