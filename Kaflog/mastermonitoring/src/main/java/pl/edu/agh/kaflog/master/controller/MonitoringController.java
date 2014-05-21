package pl.edu.agh.kaflog.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;


@Controller
public class MonitoringController {
    @Autowired
    ProducerMonitoring producerMonitoring;

    @RequestMapping(value = {"", "/", "/monitoring"})
    public String monitoring() {
        return "monitoring";
    }
}
