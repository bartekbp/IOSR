package pl.edu.agh.kaflog.master.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class StatisticsController {
    @RequestMapping("/statistics")
    public String statistics() {
        return "statistics";
    }
}
