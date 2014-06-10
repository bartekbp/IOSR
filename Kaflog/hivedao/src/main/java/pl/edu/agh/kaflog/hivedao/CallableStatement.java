package pl.edu.agh.kaflog.hivedao;

import java.sql.SQLException;
import java.sql.Statement;

public interface CallableStatement<T> {
    public T call(Statement statement) throws SQLException;
}
