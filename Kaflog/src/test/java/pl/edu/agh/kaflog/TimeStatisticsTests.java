package pl.edu.agh.kaflog;

import org.junit.Test;
import pl.edu.agh.kaflog.producer.TimeStatistics;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class TimeStatisticsTests {
    @Test
    public void secondResolutionTest() throws ParseException {
        TimeStatistics statistics = new TimeStatistics(1, 60);
        statistics.start("Apr 28 15:16:17");

        statistics.report("Apr 28 15:16:17");
        statistics.report("Apr 28 15:16:17");
        statistics.report("Apr 28 15:16:17");
        assertEquals(3, statistics.getSum("Apr 28 15:16:17"));
        statistics.report("Apr 28 15:16:18");
        statistics.report("Apr 28 15:16:19");
        statistics.report("Apr 28 15:16:20");
        statistics.report("Apr 28 15:16:21");
        assertEquals(7, statistics.getSum("Apr 28 15:16:43"));
        statistics.report("Apr 28 15:17:17");
        assertEquals(5, statistics.getSum("Apr 28 15:17:17"));
        assertEquals(4, statistics.getSum("Apr 28 15:17:18"));
        assertEquals(3, statistics.getSum("Apr 28 15:17:19"));
        assertEquals(2, statistics.getSum("Apr 28 15:17:20"));
        assertEquals(1, statistics.getSum("Apr 28 15:17:21"));
        assertEquals(0, statistics.getSum("Apr 28 15:18:18"));

        statistics.report("Apr 28 16:00:00");
        assertEquals(1, statistics.getSum("Apr 28 16:00:00"));
        assertEquals(0, statistics.getSum("Apr 28 16:05:00"));
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        statistics.report("Apr 28 16:10:00");
        assertEquals(10, statistics.getSum("Apr 28 16:10:59"));
    }

    @Test
    public void dayResolutionTest() throws ParseException {
        TimeStatistics statistics = new TimeStatistics(3600, 24);
        statistics.start("Apr 28 15:16:17");

        statistics.report("Apr 28 15:16:17");
        statistics.report("Apr 28 15:16:17");
        statistics.report("Apr 28 15:16:17");
        assertEquals(3, statistics.getSum("Apr 28 15:16:43"));
        statistics.report("Apr 28 15:16:18");
        statistics.report("Apr 28 15:16:19");
        statistics.report("Apr 28 15:16:20");
        statistics.report("Apr 28 15:16:21");
        assertEquals(7, statistics.getSum("Apr 28 15:16:43"));
        statistics.report("Apr 28 15:17:17");
        assertEquals(8, statistics.getSum("Apr 28 15:17:17"));
        assertEquals(8, statistics.getSum("Apr 28 15:17:18"));
        assertEquals(8, statistics.getSum("Apr 28 15:17:19"));
        assertEquals(8, statistics.getSum("Apr 28 15:17:20"));
        assertEquals(8, statistics.getSum("Apr 28 15:17:21"));
        assertEquals(8, statistics.getSum("Apr 28 15:18:18"));

        assertEquals(0, statistics.getSum("Apr 29 16:00:00"));
        statistics.report("Apr 29 16:10:00");
        statistics.report("Apr 29 18:10:00");
        statistics.report("Apr 29 20:10:00");
        statistics.report("Apr 29 22:10:00");
        statistics.report("Apr 30 00:10:00");
        statistics.report("Apr 30 03:10:00");
        statistics.report("Apr 30 06:10:00");
        statistics.report("Apr 30 09:10:00");
        statistics.report("Apr 30 12:10:00");
        statistics.report("Apr 30 14:10:00");
        statistics.report("Apr 30 15:16:00");
        assertEquals(11, statistics.getSum("Apr 30 15:16:16"));
    }

    @Test(expected = ParseException.class)
    public void illegalArgsTest() throws ParseException {
        TimeStatistics statistics = new TimeStatistics(1, 60);
        statistics.start("A 278 15:16:17");
    }

    public void legalArgsTest() throws ParseException {
        TimeStatistics statistics = new TimeStatistics(1, 60);
        statistics.start("May  2 15:09:02");
    }
}
