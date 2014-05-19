package pl.edu.agh.kaflog.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
//import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Controller
public class SampleController {
    @Autowired
    ProducerMonitoring producerMonitoring;

//    @Autowired
//    LogStreamConsumer logStreamConsumer;

    @RequestMapping(value={"", "/", "/monitoring"})
    public String monitoring() {
        return "monitoring";
    }

    @RequestMapping("/log_stream")
    public ModelAndView logStream() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "Hello world");
        List<String> logs = new LinkedList<String>();
//        for(LogMessage logMessage : logStreamConsumer.pollLogs()) {
//            logs.add(logMessage.toString());
//        }
        modelAndView.addObject("logs", logs);
        modelAndView.setViewName("log_stream");
        return modelAndView;
    }

    @RequestMapping("/statistics")
    public String statistics() {
        return "statistics";
    }



    @RequestMapping("/sample_raw_output")
    @ResponseBody
    public String rawOutput() {
        return "hello!";
    }

    @RequestMapping("/sample_raw_view")
    public String rawView() {
        return "welcome";
    }

    @RequestMapping("/sample_view_with_model")
    public ModelAndView viewWithModel() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "Hello world");
        List<String> clients = producerMonitoring.listClients();
        clients.addAll(Arrays.asList("fake1", "fake2"));
        modelAndView.addObject("clients", clients);
        modelAndView.setViewName("welcome");
        return modelAndView;
    }

    @RequestMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("logout", "You've been logged out successfully.");
        }
        model.setViewName("login");

        return model;
    }

}
