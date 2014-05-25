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

public class Pinger implements ExecutorUtils.ThrowingRunnable {
    private static final Logger LOG = Logger.getLogger(Pinger.class);
    private final JmxClient client;
    private KaflogProducer kaflogProducer;

    public Pinger(String masterIP, int masterPort, KaflogProducer kaflogProducer) throws JMException {
        this.client = new JmxClient(masterIP, masterPort);
        this.kaflogProducer = kaflogProducer;
    }

    @Override
    public void run() throws Exception {
        long currentTime = KaflogDateUtils.getCurrentTime();
        client.invokeOperation(
                new ObjectName("RegisterClientMBean", "name", "RegisterClientMBean"),
                "registerClient",
                new NodeState(Inet4Address.getLocalHost().getHostAddress(),
                        Inet4Address.getLocalHost().getHostName(),
                        currentTime - kaflogProducer.getStartTime(),
                        kaflogProducer.getTotalLogs(),
                        kaflogProducer.getLogsInLastDay(currentTime),
                        kaflogProducer.getLogsInLastHour(currentTime),
                        kaflogProducer.getLogsInLastMinute(currentTime)));
    }
}
