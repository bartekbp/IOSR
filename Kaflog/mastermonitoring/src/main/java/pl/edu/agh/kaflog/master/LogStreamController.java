package pl.edu.agh.kaflog.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;


@Controller
public class LogStreamController {
    @Autowired
    ProducerMonitoring producerMonitoring;

    @Autowired
    LogStreamConsumer logStreamConsumer;

    @RequestMapping("/log_stream")
    public ModelAndView logStream() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "Hello world");
//        List<String> logs = new LinkedList<String>();
//        for(LogMessage logMessage : logStreamConsumer.pollLogs()) {
//            logs.add(logMessage.toString());
//        }
//        modelAndView.addObject("logs", logs);
        modelAndView.setViewName("log_stream");
        return modelAndView;
    }

    @RequestMapping(value = "/poll_logs", method = RequestMethod.GET)
    public @ResponseBody
    String pollLogs() {
        StringBuilder sb = new StringBuilder();
        for(LogMessage logMessage : logStreamConsumer.pollLogs()) {
            sb.append(renderLog(logMessage));
        }
        return sb.toString();
    }

    private String renderLog(LogMessage logMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");

        // Date
        sb.append("<td>");
        sb.append(KaflogDateUtils.millisToDate(logMessage.getTimestamp()));
        sb.append("</td>");

        // Node
        sb.append("<td>");
        sb.append(logMessage.getHostname());
        sb.append("</td>");

        // Severity
        sb.append("<td>");
        sb.append(LogMessage.LEVEL_STRING[logMessage.getSeverity()]);
        sb.append("</td>");

        // Facility
        sb.append("<td>");
        sb.append(LogMessage.FACILITY_STRING[logMessage.getFacility()]);
        sb.append("</td>");

        // Process
        sb.append("<td>");
        sb.append(logMessage.getSource());
        sb.append("</td>");

        // Message
        sb.append("<td>");
        sb.append(logMessage.getMessage());
        sb.append("</td>");

        sb.append("</tr>");
        return sb.toString();
    }
}
