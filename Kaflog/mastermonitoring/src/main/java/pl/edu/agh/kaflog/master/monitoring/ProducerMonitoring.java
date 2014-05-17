package pl.edu.agh.kaflog.master.monitoring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.management.JMException;
import java.util.List;

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
}
