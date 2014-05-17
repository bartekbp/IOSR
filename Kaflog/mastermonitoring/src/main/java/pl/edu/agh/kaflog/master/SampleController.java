package pl.edu.agh.kaflog.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.util.Arrays;
import java.util.List;

@Controller
public class SampleController {
    @Autowired
    ProducerMonitoring producerMonitoring;

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
}
