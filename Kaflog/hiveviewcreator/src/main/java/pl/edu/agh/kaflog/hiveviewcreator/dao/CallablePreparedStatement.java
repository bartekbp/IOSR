package pl.edu.agh.kaflog.hiveviewcreator.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface CallablePreparedStatement<T> {
    public String query();
    public T call(PreparedStatement preparedStatement) throws SQLException;
}
