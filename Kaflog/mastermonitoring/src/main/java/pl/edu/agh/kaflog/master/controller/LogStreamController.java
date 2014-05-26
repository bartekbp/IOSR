package pl.edu.agh.kaflog.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;
import pl.edu.agh.kaflog.master.logs.LogStreamConsumer;
import pl.edu.agh.kaflog.master.logs.LogStreamMaster;

import java.util.LinkedList;


@Controller
public class LogStreamController {

    @Autowired
    LogStreamMaster logStreamMaster;

    @RequestMapping("/log_stream")
    public ModelAndView logStream() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("log_stream");
        return modelAndView;
    }

    // Used for ajax polling for logs. Returns data in format
    // <timestamp>;<rendered_table_row>
    // timestamp is the timestamp of newest log
    // The page should parse out the timestamp and ask for new logs with updated timestamp
    @RequestMapping(value = "/poll_logs", method = RequestMethod.GET)
    public @ResponseBody
    String pollLogs(@RequestParam(value = "since", required = false) long since,
                    @RequestParam(value = "limit", required = false) int limit) {

        LinkedList<LogMessage> newLogs = logStreamMaster.pollLogs(since, limit);
        if (newLogs.size() == 0) {
            return since + ";";
        }

        StringBuilder sb = new StringBuilder(newLogs.getLast().getTimestamp() + ";");
        for(LogMessage logMessage : newLogs) {
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
