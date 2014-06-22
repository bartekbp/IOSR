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

/**
 * This been is used to register kafka node to master node
 */
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

    public List<NodeStateSummary> listClients() {
        return registerClientMBean.listClients();
    }
}
