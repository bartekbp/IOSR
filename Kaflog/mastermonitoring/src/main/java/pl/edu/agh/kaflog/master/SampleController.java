package pl.edu.agh.kaflog.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.util.Arrays;
import java.util.List;

//import pl.edu.agh.kaflog.common.LogMessage;

@Controller
public class SampleController {
    @Autowired
    ProducerMonitoring producerMonitoring;

    @RequestMapping(value={"", "/", "/monitoring"})
    public String monitoring() {
        return "monitoring";
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
