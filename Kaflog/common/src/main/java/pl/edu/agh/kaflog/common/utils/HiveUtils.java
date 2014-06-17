package pl.edu.agh.kaflog.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import pl.edu.agh.kaflog.common.KaflogConstants;

import java.io.Serializable;

public class HiveUtils implements Serializable {
    private static final String lowerKeyPadding = StringUtils.repeat(" ", 30);
    private static final String upperKeyPadding = StringUtils.repeat("z", 30);

    /**
     * HBase is Key-Value storage so it is needed to encode all data in key
     * @param hiveTimestamp
     * @param host
     * @param severity
     * @return generated HBase key
     */
    public String toHBaseKey(String hiveTimestamp, String host, String severity) {
        return hiveTimestamp + StringUtils.rightPad(host, 20, '0') + StringUtils.rightPad(severity, 10, '0');
    }

    /**
     * Converts HBase key to severity value
     * @param hBaseKey HBase key that is build of timestamp hostname and severity
     * @return severity value puled out of hBaseKey
     */
    public String hBaseKeyToSeverity(String hBaseKey) {
        return StringUtils.stripEnd(hBaseKey.substring(hBaseKey.length() - 10), "0");
    }

    /**
     * Converts HBase key to hostname
     * @param hBaseKey HBase key that is build of timestamp hostname and severity
     * @return hostname puled out of hBaseKey
     */
    public String hBaseKeyToHost(String hBaseKey) {
        return StringUtils.stripEnd(hBaseKey.substring(hBaseKey.length() - 30, hBaseKey.length() - 10), "0");
    }

    /**
     * Return key prefix with upper time bound.
     * Key is build of timestamp hostname and severity, lookup at key prefix allow to
     * query keys only by timestamp.
     * @param date upper bound
     * @return key prefix
     */
    public String toHBaseUpperKeyBound(DateTime date) {
        return toHiveTimestamp(date) + upperKeyPadding;
    }

    /**
     * Return key prefix with lower time bound.
     * Key is build of timestamp hostname and severity, lookup at key prefix allow to
     * query keys only by timestamp.
     * @param date upper bound
     * @return key prefix
     */
    public String toHBaseLowerKeyBound(DateTime date) {
        return toHiveTimestamp(date) + lowerKeyPadding;
    }

    /**
     * Converts jodaTime DateTime to hiveTimestamp
     * @param date to be converted
     * @return hiveTimestamp
     */
    public String toHiveTimestamp(DateTime date) {
        return toHiveTimestamp(date.getMillis());
    }

    /**
     * Convert time in millisecond from unix epoch to hiveTimestamp
     * @param milliseconds
     * @return hiveTimestamp
     */
    public String toHiveTimestamp(long milliseconds) {
        //hive timestamp has seconds resolution
        return String.valueOf(milliseconds / KaflogConstants.MILLISECONDS_IN_SECOND);
    }



}
