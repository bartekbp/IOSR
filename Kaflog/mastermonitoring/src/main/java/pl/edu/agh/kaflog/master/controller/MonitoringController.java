package pl.edu.agh.kaflog.master.controller;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;
import pl.edu.agh.kaflog.master.monitoring.NodeStateSummary;
import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;
import pl.edu.agh.kaflog.master.monitoring.RegisterClientMBean;
import pl.edu.agh.kaflog.master.statistics.ImpalaHBaseDao;

import java.sql.SQLException;
import java.util.List;


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
    String pollMonitoring()  {
        List<NodeStateSummary> nodeStateSummaries = producerMonitoring.listClients();

        if (nodeStateSummaries.size() == 0) {
            return "";
        }

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
        if (lastHeartBeatAgo < RegisterClientMBean.PRODUCER_CONTROL_PERIOD) lastHeartBeatAgo = 0;
        sb.append(lastHeartBeatAgo / 1000);
        sb.append(";");
        sb.append(nodeStateSummary.getUptime() / 1000);
        sb.append(";");
        sb.append(nodeStateSummary.getLogsInLastMinute());
        sb.append(";");
        sb.append(nodeStateSummary.getLogsInLastHour());
        sb.append(";");
        sb.append(nodeStateSummary.getLogsInLastDay());
        sb.append(";");
        sb.append(nodeStateSummary.getTotalLogs());
        return sb.toString();
    }
}
