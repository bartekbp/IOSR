package pl.edu.agh.kaflog.master.monitoring;


import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.agh.kaflog.common.NodeState;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@JmxResource(beanName = "RegisterClientMBean", domainName = "RegisterClientMBean")
public class RegisterClientMBean {
    private static final Logger log = LoggerFactory.getLogger(RegisterClientMBean.class);

    // How often client list is checked for old entries
    public static final int PURGE_PERIOD = 5000; // ms

    // How long till assumption that client is having connection problems
    public static final long PRODUCER_CONTROL_PERIOD = 10000; //ms

    // How long till assumption that client is offline
    public static final long PRODUCER_TIMEOUT = 120000; //ms

    private ConcurrentHashMap<String, NodeStateSummary> clients = new ConcurrentHashMap<String, NodeStateSummary>();
    private long lastPurge = 0;

    @JmxOperation(description = "registerClient")
    public void registerClient(NodeState nodeState) {
        log.info("New ping from " + nodeState.getHostname());
        if (!clients.contains(nodeState.getHostname())) {
            clients.put(nodeState.getHostname(), new NodeStateSummary(nodeState.getHostname(), nodeState.getIp()));
        }
        clients.get(nodeState.getHostname()).update(nodeState);
    }

    public List<NodeStateSummary> listClients() {
        purgeClientList();
        LinkedList<NodeStateSummary> result = new LinkedList<NodeStateSummary>(clients.values());
        Collections.sort(result);
        return result;
    }

    private void purgeClientList() {
        if (KaflogDateUtils.getCurrentTime() - lastPurge > PURGE_PERIOD) {
            List<String> keySet = new LinkedList<String>(clients.keySet());
            for (String key : keySet) {
                if (KaflogDateUtils.getCurrentTime() - clients.get(key).getLastHeartbeat() > PRODUCER_TIMEOUT) {
                    clients.remove(key);
                }
            }
            lastPurge = KaflogDateUtils.getCurrentTime();
        }
    }
}
