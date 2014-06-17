package pl.edu.agh.kaflog.stormconsumer.utils;

/**
 * Names of fields in tuple passed in storm topology
 */
public interface StormFields {
    /**
     * Field with {@link pl.edu.agh.kaflog.common.LogMessage}
     */
    public static final String LOG_MESSAGE = "logMessage";
    /**
     * Field with String contain HBase row id
     */
    public static final String ROWID = "rowid";
}
