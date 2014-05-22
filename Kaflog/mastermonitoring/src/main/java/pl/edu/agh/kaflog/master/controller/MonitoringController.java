package pl.edu.agh.kaflog.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.master.monitoring.NodeState;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.util.List;
import java.util.Random;


@Controller
public class MonitoringController {
    @Autowired
    ProducerMonitoring producerMonitoring;

    @RequestMapping(value = {"", "/", "/monitoring"})
    public ModelAndView monitoring() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("monitoring");
        return modelAndView;
    }


    // Used for ajax updates. Returns data in format of rendered_table_rows, to be inserted in
    // the main table of monitoring page
    @RequestMapping(value = "/poll_monitoring", method = RequestMethod.GET)
    public
    @ResponseBody
    String pollMonitoring() {
        List<NodeState> nodeStates = producerMonitoring.mockListClients();

        StringBuilder sb = new StringBuilder("<tr>");
        for (NodeState nodeState : nodeStates) {
            sb.append(renderRow(nodeState));
        }
        sb.append("</tr>");
        return sb.toString();
    }

    private String renderRow(NodeState nodeState) {
        StringBuilder sb = new StringBuilder("<td>" + new Random().nextInt(10000) + "</td>");
        return sb.toString();
    }
}
