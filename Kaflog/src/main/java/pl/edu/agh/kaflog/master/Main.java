package pl.edu.agh.kaflog.master;


import pl.edu.agh.kaflog.master.monitoring.ProducerMonitoring;

import javax.management.JMException;

public class Main {
    public static void main(String... args) throws JMException {
        ProducerMonitoring producerMonitoring = new ProducerMonitoring("10.211.55.102", 9010);
        System.out.println("Producer ip: " + producerMonitoring.getIP());
    }
}
