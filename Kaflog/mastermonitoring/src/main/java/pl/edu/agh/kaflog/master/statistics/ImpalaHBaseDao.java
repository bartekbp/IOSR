package pl.edu.agh.kaflog.master.statistics;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang.StringUtils;
import org.joda.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import pl.edu.agh.kaflog.common.utils.HiveUtils;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates layers join (batch and speed)
 * Uses storm and hadoop tables to generate report data
 */
@Repository
public class ImpalaHBaseDao  {
    @Autowired
    private NamedParameterJdbcTemplate jdbc;
    @Autowired
    private HiveUtils hiveUtils;
    @Value("${hdp.table}")
    private String hdpTable;
    @Value("${srm.table}")
    private String srmTable;


    public ImpalaHBaseDao() {
    }

    /**
     * Returns number of logs per host and severity.
     * @param from start time
     * @param to end time
     * @return Map - host to map - severity to number of logs (Map<HostName, Map<SeverityLevel, NumberOfLogs>>)
     */
    public Map<String, Map<String, Long>> getHostSeverityResults(DateTime from, DateTime to) {
        DateTime now = new DateTime().toDateTime(DateTimeZone.UTC);
        to = to.isAfterNow() ? now : to;
        DateTime hadoopFrom;
        DateTime hadoopTo;
        DateTime stormFrom;
        DateTime stormTo;

        // Splits time range to parts that can be read using speed layer and hadoop layer
        Interval interval = new Interval(to, now);
        if (interval.toDuration().isShorterThan(Duration.standardHours(2L))) {
            stormTo = to;
            if(from.isBefore(now.minusHours(2))) {
                stormFrom = now.minusHours(2).withMinuteOfHour(00);
                hadoopTo = stormFrom;
                hadoopFrom = from.withMinuteOfHour(00);
            } else {
                stormFrom = from;
                hadoopTo = now;
                hadoopFrom = now;
            }
        } else {
            stormFrom = now;
            stormTo = now;
            hadoopTo = to.withMinuteOfHour(00);
            hadoopFrom = from.withMinuteOfHour(00);
        }

        Map<String, AtomicLongMap<String>> hostToSevToLogNum = new HashMap<String, AtomicLongMap<String>>();

        //if storm is needed
        if (stormFrom.isBefore(stormTo)) {
            queryForHostAndSeverity(stormFrom, stormTo, srmTable, hostToSevToLogNum);
        }

        //if hadoop is needed
        if (hadoopFrom.isBefore(hadoopTo)) {
            queryForHostAndSeverity(hadoopFrom, hadoopTo, hdpTable, hostToSevToLogNum);
        }

        // Turns result map to Immutable map
        return ImmutableMap.copyOf(Maps.transformValues(hostToSevToLogNum, new Function<AtomicLongMap<String>, Map<String, Long>>() {
            @Override
            public Map<String, Long> apply(AtomicLongMap<String> input) {
                return ImmutableMap.copyOf(input.asMap());
            }
        }));
    }

    /**
     * Query host severity data for given time frime from given table
     * @param from start time
     * @param to end time
     * @param table table to be queried
     * @param hostToSevToLogNum map where data is stored, actually data is added to data that exists in map previously
     *                          that allow to easily merge results from multiple tables (in our case number of logs is
     *                          just additive number)
     */
    private void queryForHostAndSeverity(DateTime from, DateTime to, String table, final Map<String, AtomicLongMap<String>> hostToSevToLogNum) {
        //time ranges are converted to masked timestamps that allow query for a range
        jdbc.query("select key as k, count as s from " + table +
                        " where key >= :from and key < :to",
                ImmutableMap.of("from", hiveUtils.toHBaseLowerKeyBound(from),
                        "to", hiveUtils.toHBaseUpperKeyBound(to)), new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet resultSet) throws SQLException {
                        String timeHostSeverity = resultSet.getString(1);
                        String host = hiveUtils.hBaseKeyToHost(timeHostSeverity);
                        String severity = hiveUtils.hBaseKeyToSeverity(timeHostSeverity);
                        long count = resultSet.getLong(2);

                        if (!hostToSevToLogNum.containsKey(host)) {
                            hostToSevToLogNum.put(host, AtomicLongMap.<String>create());
                        }

                        hostToSevToLogNum.get(host).addAndGet(severity, count);
                    }
                }
        );
    }
}
