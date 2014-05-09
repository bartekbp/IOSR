package pl.edu.agh.kaflog.utils;



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
