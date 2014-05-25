package pl.edu.agh.kaflog.master.monitoring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;

import javax.annotation.PostConstruct;
import javax.management.JMException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Component
public class ProducerMonitoring {
    private static final Logger log = LoggerFactory.getLogger(ProducerMonitoring.class);
    @Autowired
    private RegisterClientMBean registerClientMBean;
    @Autowired
    MBeanPublisher mBeanPublisher;

    @PostConstruct
    private void init() throws JMException {
        mBeanPublisher.registerObject(registerClientMBean);
    }

    public List<String> listClients() {
        return registerClientMBean.listClients();
    }


    // TODO this should return a real list of node states for nodes in the cluster
    public List<NodeState> mockListClients() {
        setRandomState(mockState1);
        setRandomState(mockState2);
        setRandomState(mockState3);
        List<NodeState> result = new LinkedList<NodeState>();
        result.add(mockState2);
        result.add(mockState1);
        result.add(mockState3);
        Collections.sort(result);
        return result;
    }

    private NodeState mockState1 = new NodeState("10.200.150.1", "kafka-node1", currentTimeMinusSecs(5), 1000, 10000, 1000, 100, 10);
    private NodeState mockState2 = new NodeState("10.200.150.2", "kafka-node2", currentTimeMinusSecs(5), 501001, 10000, 1000, 100, 10);
    private NodeState mockState3 = new NodeState("10.200.150.3", "kafka-node3", currentTimeMinusSecs(5), 2342352, 10000, 1000, 100, 10);

    private void setRandomState(NodeState nodeState) {
        nodeState.setLastHeartbeat(currentTimeMinusSecs(new Random().nextInt(14)));
        nodeState.setUptime(nodeState.getUptime() + 1);
        nodeState.setTotalLogs(new Random().nextInt((int) (300 * nodeState.getUptime())));
        nodeState.setLogsInlastDay(new Random().nextInt(300 * 86400));
        nodeState.setLogsInLastHour(new Random().nextInt(300 * 3600));
        nodeState.setLogsInLastMinute(new Random().nextInt(300 * 60));
    }

    private long currentTimeMinusSecs(int secs) {
        return KaflogDateUtils.getCurrentTime() - secs * 1000;
    }
}
