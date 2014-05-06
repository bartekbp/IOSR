package pl.edu.agh.kaflog.master.monitoring;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.j256.simplejmx.client.JmxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.producer.Main;
import pl.edu.agh.kaflog.utils.ExecutorUtils;


import javax.management.JMException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProducerMonitoring {
    private static final Logger log = LoggerFactory.getLogger(ProducerMonitoring.class);
    private static final RegisterClientMBean registerClientMBean = new RegisterClientMBean();

    public static ExecutorUtils.ThrowingRunnable initTask() {
        return new ExecutorUtils.ThrowingRunnable() {
            @Override
            public void run() throws JMException {
                MBeanPublisher publisher = MBeanPublisher.withPort(2997);
                publisher.startServer();
                publisher.registerObject(registerClientMBean);
            }
        };
    }

    public List<String> listClients() {
        return registerClientMBean.listClients();
    }
}
