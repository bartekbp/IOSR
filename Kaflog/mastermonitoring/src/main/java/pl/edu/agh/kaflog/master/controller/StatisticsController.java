package pl.edu.agh.kaflog.master.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.master.statistics.ViewQueryHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class StatisticsController {
    private static final DateFormat format = DateFormat.getDateInstance();
    private final ViewQueryHandler viewQueryHandler = new ViewQueryHandler();

    @RequestMapping("/statistics")
    public ModelAndView statistics(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "from_hour", required = false) String from_hour,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "to_hour", required = false) String to_hour)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("statistics");
       // if(from != null) {
            modelAndView.addObject("report", createReport(from, from_hour, to, to_hour));
       // }
        modelAndView.setViewName("statistics");
        return modelAndView;
    }

    private Object createReport(String from, String from_hour, String to, String to_hour) {
//        try {
           // Date fromDate = format.parse(from + " " + from_hour);
           // Date toDate = format.parse(to + " " + to_hour);
            return viewQueryHandler.createView(null, null);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
    }


}
