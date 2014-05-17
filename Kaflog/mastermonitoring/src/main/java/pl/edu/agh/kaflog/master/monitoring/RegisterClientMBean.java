package pl.edu.agh.kaflog.master.monitoring;


import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
@JmxResource(beanName = "RegisterClientMBean", domainName = "RegisterClientMBean")
public class RegisterClientMBean {
    private Collection<String> clients = new ConcurrentSkipListSet<String>();

    @JmxOperation(description = "registerClient")
    public void registerClient(String address) {
        clients.add(address);
    }

    public List<String> listClients() {
        return new LinkedList<String>(clients);
    }
}
