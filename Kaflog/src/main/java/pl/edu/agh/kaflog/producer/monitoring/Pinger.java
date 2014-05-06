package pl.edu.agh.kaflog.producer.monitoring;


import com.j256.simplejmx.client.JmxClient;
import org.apache.log4j.Logger;
import pl.edu.agh.kaflog.utils.ExecutorUtils;

import javax.management.JMException;
import javax.management.ObjectName;
import java.net.Inet4Address;

public class Pinger implements ExecutorUtils.ThrowingRunnable {
    private static final Logger LOG = Logger.getLogger(Pinger.class);
    private final JmxClient client;

    public Pinger(String masterIP, int masterPort) throws JMException {
        this.client = new JmxClient(masterIP, masterPort);
    }

    @Override
    public void run() throws Exception {
        client.invokeOperationToString(new ObjectName("RegisterClientMBean", "name", "RegisterClientMBean"), "registerClient", Inet4Address.getLocalHost().getHostAddress());
    }
}
