package pl.edu.agh.kaflog.master.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;
import pl.edu.agh.kaflog.master.monitoring.NodeStateSummary;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import java.util.List;


@Controller
public class MonitoringController {
    private final static long PRODUCER_TIMEOUT = 10000; // in ms

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
        List<NodeStateSummary> nodeStateSummaries = producerMonitoring.mockListClients();

        StringBuilder sb = new StringBuilder();
        for (NodeStateSummary nodeStateSummary : nodeStateSummaries) {
            sb.append(renderRow(nodeStateSummary));
            sb.append("+");
        }
        return sb.substring(0, sb.length() - 1);
    }

    // Tokens:
    // 0: hostname
    // 1: ipAddress
    // 2: offlineFor
    // 3: uptime
    // 4: lastMinuteLogs
    // 5: lastHourLogs
    // 6: lastDayLogs
    // 7: overallLogs
    private String renderRow(NodeStateSummary nodeStateSummary) {
        StringBuilder sb = new StringBuilder();
        sb.append(nodeStateSummary.getHostname());
        sb.append(";");
        sb.append(nodeStateSummary.getIp());
        sb.append(";");
        long lastHeartBeatAgo = KaflogDateUtils.getCurrentTime() - nodeStateSummary.getLastHeartbeat();
        if (lastHeartBeatAgo < PRODUCER_TIMEOUT) lastHeartBeatAgo = 0;
        sb.append(lastHeartBeatAgo / 1000);
        sb.append(";");
        sb.append(nodeStateSummary.getUptime());
        sb.append(";");
        sb.append(nodeStateSummary.getLogsInLastMinute());
        sb.append(";");
        sb.append(nodeStateSummary.getLogsInLastHour());
        sb.append(";");
        sb.append(nodeStateSummary.getLogsInlastDay());
        sb.append(";");
        sb.append(nodeStateSummary.getTotalLogs());
        return sb.toString();
    }
}
