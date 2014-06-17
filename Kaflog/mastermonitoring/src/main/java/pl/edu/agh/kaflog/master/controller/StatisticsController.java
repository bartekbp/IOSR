package pl.edu.agh.kaflog.master.controller;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.master.statistics.ViewQueryHandler;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class StatisticsController {
    private static final DateFormat format = DateFormat.getDateInstance();
    @Autowired
    private ViewQueryHandler viewQueryHandler;

    public StatisticsController()  {
    }

    @RequestMapping("/statistics")
    public ModelAndView statistics(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) throws SQLException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("statistics");
        if(to != null) {
            modelAndView.addObject("report", createReport(from, to));
        }
        modelAndView.setViewName("statistics");
        return modelAndView;
    }

    private Object createReport(String from, String to) throws SQLException {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        DateTime fromDate = null;
        DateTime toDate = null;

        try {
            fromDate = fmt.parseDateTime(String.valueOf(from));
            toDate = fmt.parseDateTime(String.valueOf(to));
        } catch(Throwable t) {
            t.printStackTrace();
        }
        if (fromDate == null) {
            fromDate = new DateTime().hourOfDay().addToCopy(-1);
        }
        if (toDate == null) {
            toDate = new DateTime();
        }
        return viewQueryHandler.createView(fromDate.withZoneRetainFields(DateTimeZone.UTC), toDate.withZoneRetainFields(DateTimeZone.UTC));

    }


}
