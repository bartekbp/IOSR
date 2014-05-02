package pl.edu.agh.kaflog.master.monitoring;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.j256.simplejmx.client.JmxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.management.JMException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProducerMonitoring {
    private static final Logger log = LoggerFactory.getLogger(ProducerMonitoring.class);

    private final JmxClient client;

    public ProducerMonitoring(String host, int port) throws JMException {
        client = new JmxClient(host, port);
    }


    public List<String> listBeans() {
        try {
            return Lists.transform(new ArrayList<>(client.getBeanNames()), new Function<ObjectName, String>() {
                @Override
                public String apply(ObjectName objectName) {
                    return objectName.getCanonicalName();
                }
            });
        } catch (JMException e) {
            log.error(e.toString());
            return Collections.emptyList();
        }
    }

    public String getIP() {
        try {
            return client.getAttributeString(new ObjectName("IPProvider", "name", "IPProvider"), "iP");
        } catch (Exception e) {
            log.error(e.toString());
            return "";
        }
    }

}
