package pl.edu.agh.kaflog.common.utils;



public class CloseableUtils {
    public static void close(AutoCloseable autoCloseable) {
        if(autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
