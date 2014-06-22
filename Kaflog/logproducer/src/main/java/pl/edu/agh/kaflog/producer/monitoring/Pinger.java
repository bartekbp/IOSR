package pl.edu.agh.kaflog.producer.monitoring;


import com.j256.simplejmx.client.JmxClient;
import org.apache.log4j.Logger;
import pl.edu.agh.kaflog.common.NodeState;
import pl.edu.agh.kaflog.common.utils.ExecutorUtils;
import pl.edu.agh.kaflog.common.utils.KaflogDateUtils;
import pl.edu.agh.kaflog.producer.kafka.KaflogProducer;

import javax.management.JMException;
import javax.management.ObjectName;
import java.net.Inet4Address;

/**
 * Task that cyclic report node state to master node
 */
public class Pinger implements ExecutorUtils.ThrowingRunnable {
    private static final Logger LOG = Logger.getLogger(Pinger.class);
    private JmxClient client;
    private KaflogProducer kaflogProducer;
    private String masterIP;
    private int masterPort;

    /**
     *
     * @param masterIP - master node ip
     * @param masterPort - master node JMX port
     * @param kaflogProducer - used to get node state
     */
    public Pinger(String masterIP, int masterPort, KaflogProducer kaflogProducer) {
        this.masterIP = masterIP;
        this.masterPort = masterPort;
        this.client = null;
        this.kaflogProducer = kaflogProducer;
    }

    /**
     * Creates JMX client
     * @throws JMException
     */
    private void lazyInit() throws JMException {
        if (client == null) {
            JmxClient client = new JmxClient(masterIP, masterPort);
            this.client = client;
        }
    }

    /**
     * This method is called in circle. Pings master node and reports current state
     * @throws Exception
     */
    @Override
    public void run() throws Exception {
        lazyInit();
        long currentTime = KaflogDateUtils.getCurrentTime();
        client.invokeOperation(
                new ObjectName("RegisterClientMBean", "name", "RegisterClientMBean"),
                "registerClient",
                new NodeState(Inet4Address.getLocalHost().getHostAddress(),
                        Inet4Address.getLocalHost().getHostName(),
                        kaflogProducer.getStartTime(),
                        kaflogProducer.getTotalLogs(),
                        kaflogProducer.getLogsInLastDay(currentTime),
                        kaflogProducer.getLogsInLastHour(currentTime),
                        kaflogProducer.getLogsInLastMinute(currentTime)));
    }
}
