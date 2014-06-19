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

    public Map<String, Map<String,Long>> getHostSeverityResults(DateTime from, DateTime to) {
        DateTime now = new DateTime().toDateTime(DateTimeZone.UTC);
        to = to.isAfterNow() ? now : to;
        DateTime hadoopFrom;
        DateTime hadoopTo;
        DateTime stormFrom;
        DateTime stormTo;

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

        if (stormFrom.isBefore(stormTo)) {
            queryForHostAndSeverity(stormFrom, stormTo, srmTable, hostToSevToLogNum);
        }

        if (hadoopFrom.isBefore(hadoopTo)) {
            queryForHostAndSeverity(hadoopFrom, hadoopTo, hdpTable, hostToSevToLogNum);
        }


        return ImmutableMap.copyOf(Maps.transformValues(hostToSevToLogNum, new Function<AtomicLongMap<String>, Map<String, Long>>() {
            @Override
            public Map<String, Long> apply(AtomicLongMap<String> input) {
                return ImmutableMap.copyOf(input.asMap());
            }
        }));
    }

    private void queryForHostAndSeverity(DateTime from, DateTime to, String table, final Map<String, AtomicLongMap<String>> hostToSevToLogNum) {
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


    public Map<String, Long> getSeverityResults(DateTime from, DateTime to)  {
        Map<String, Map<String, Long>> hostSevToCount = getHostSeverityResults(from, to);
        return getSeverityResults(hostSevToCount);
    }

    public Map<String, Long> getSeverityResults(Map<String, Map<String, Long>> hostSevToCount)  {
        AtomicLongMap<String> sevToTime = AtomicLongMap.<String>create();
        for(Map<String, Long> value : hostSevToCount.values()) {
            for(String sev : value.keySet()) {
                sevToTime.addAndGet(sev, value.get(sev));
            }
        }

        return ImmutableMap.copyOf(sevToTime.asMap());
    }

    public Map<String, Long> getHostResults(DateTime from, DateTime to)  {
        Map<String, Map<String, Long>> hostSevToCount = getHostSeverityResults(from, to);
        return getHostResults(hostSevToCount);
    }

    public Map<String, Long> getHostResults(Map<String, Map<String, Long>> hostSevToCount)  {
        AtomicLongMap<String> hostToCount = AtomicLongMap.<String>create();
        for(Map.Entry<String, Map<String, Long>> entry : hostSevToCount.entrySet()) {
            for(Long count : entry.getValue().values()) {
                hostToCount.addAndGet(entry.getKey(), count);
            }
        }

        return ImmutableMap.copyOf(hostToCount.asMap());
    }
}
