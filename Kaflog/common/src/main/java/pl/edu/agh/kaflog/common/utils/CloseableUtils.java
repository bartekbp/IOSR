package pl.edu.agh.kaflog.common.utils;


import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * In Java 6 there is no try with resource statement
 * This class allows easy closing all entities that needs it
 *
 * Another issue is that Java 6 des not have AutoCloseable interface so we need
 * to implements many signatures
 */
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
