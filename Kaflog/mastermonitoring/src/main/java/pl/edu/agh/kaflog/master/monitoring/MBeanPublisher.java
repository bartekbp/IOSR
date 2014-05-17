package pl.edu.agh.kaflog.master.monitoring;


import com.j256.simplejmx.server.JmxServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.JMException;

@Component
public class MBeanPublisher {
    @Value("${application.mBeanPublisher.port}")
    private int port;
    private JmxServer jmxServer;

    @PostConstruct
    private void startServer() throws JMException {
        jmxServer = new JmxServer(port);
        jmxServer.start();
    }

    @PreDestroy
    public void destroy() {
        jmxServer.stop();
    }

    public void registerObject(Object object) throws JMException {
        jmxServer.register(object);
    }

}
