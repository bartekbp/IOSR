package pl.edu.agh.kaflog.master.monitoring;


import com.j256.simplejmx.common.JmxAttributeMethod;
import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import pl.edu.agh.kaflog.producer.Main;

import javax.management.JMException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

@JmxResource(beanName = "RegisterClientMBean", domainName = "RegisterClientMBean")
public class RegisterClientMBean {
    private Collection<String> clients = new ConcurrentSkipListSet<>();

    @JmxOperation(description = "registerClient")
    public void registerClient(String address) {
        clients.add(address);
    }

    public List<String> listClients() {
        return new LinkedList<>(clients);
    }
}
