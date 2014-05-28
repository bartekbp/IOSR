package pl.edu.agh.kaflog.common.utils;


import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CloseableUtils {
    public static void close(Closeable autoCloseable) {
        if(autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void close(Statement statement) {
        if(statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void close(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
