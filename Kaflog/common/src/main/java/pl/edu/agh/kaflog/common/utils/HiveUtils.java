package pl.edu.agh.kaflog.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.Serializable;

public class HiveUtils implements Serializable {
    private static final String lowerKeyPadding = StringUtils.repeat(" ", 30);
    private static final String upperKeyPadding = StringUtils.repeat("z", 30);

    public String toHBaseKey(String hiveTimestamp, String host, String severity) {
        return hiveTimestamp + StringUtils.rightPad(host, 20, '0') + StringUtils.rightPad(severity, 10, '0');
    }

    public String hBaseKeyToSeverity(String timeHostSeverity) {
        return StringUtils.stripEnd(timeHostSeverity.substring(timeHostSeverity.length() - 10), "0");
    }

    public String hBaseKeyToHost(String timeHostSeverity) {
        return StringUtils.stripEnd(timeHostSeverity.substring(timeHostSeverity.length() - 30, timeHostSeverity.length() - 10), "0");
    }

    public String toHBaseUpperKeyBound(DateTime date) {
        return toHiveTimestamp(date) + upperKeyPadding;
    }

    public String toHBaseLowerKeyBound(DateTime date) {
        return toHiveTimestamp(date) + lowerKeyPadding;
    }

    public String toHiveTimestamp(DateTime date) {
        return toHiveTimestamp(date.getMillis());
    }

    public String toHiveTimestamp(long milliseconds) {
        return String.valueOf(milliseconds / hiveTimestampUnit());
    }

    public int hiveTimestampUnit() {
        return 1000;
    }

}
