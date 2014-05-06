package pl.edu.agh.kaflog.producer.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.producer.Main;
import pl.edu.agh.kaflog.utils.KaflogProperties;
import pl.edu.agh.kaflog.utils.ExecutorUtils;

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Properties;

/**
 * We could replace this class with:
 * https://github.com/xstevens/syslog-kafka/blob/master/src/main/java/kafka/syslog/SyslogKafkaServer.java
 * (in this way we could achieve better log parsing and higher level api)
 */
public class KaflogProducer implements ExecutorUtils.ThrowingRunnable {
    private static Logger log = LoggerFactory.getLogger(KaflogProducer.class);
    private Producer<Integer, LogMessage> producer;
    private Properties props = new Properties();
    DatagramSocket socket;

    private final TimeStatistics lastMinute = new TimeStatistics(1, 60);
    private final TimeStatistics lastHour = new TimeStatistics(60, 60);
    private final TimeStatistics lastDay = new TimeStatistics(3600, 24);

    public KaflogProducer() {
        String brokersList = KaflogProperties.getProperty("kaflog.kafka.brokersList");
        props.put("metadata.broker.list", brokersList);
        props.put("serializer.class", "pl.edu.agh.kaflog.common.LogMessageSerializer");
        props.put("request.required.acks", "1");
        producer = new Producer<>(new ProducerConfig(props));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    log.info("Attempting graceful producer shutdown");
                    producer.close();
                    socket.close();
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        });
    }

    public void run() throws IOException, ParseException {
        socket = new DatagramSocket(5001, InetAddress.getByName("127.0.0.1"));
        log.info("Listen on " + socket.getLocalAddress());
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        lastMinute.start(TimeStatistics.getCurrentDate());
        lastHour.start(TimeStatistics.getCurrentDate());
        lastDay.start(TimeStatistics.getCurrentDate());

        long timer = System.currentTimeMillis();
        String topic = KaflogProperties.getProperty("kaflog.kafka.topic");
        while (true) {
            socket.receive(packet);
            // Data is in format:
            // <xxx>mmm dd hh:mm:ss hostname user: message
            String data = new String(packet.getData(), 0, packet.getLength());
            int parPos = data.indexOf('>');
            int facilityAndSeverity = Integer.parseInt(data.substring(1, parPos));
            data = data.substring(parPos + 1);
            String[] tokens = data.split("\\s+", 6);
            LogMessage logMessage = new LogMessage(
                    facilityAndSeverity / 8, // facility
                    facilityAndSeverity % 8, // severity
                    String.format("%s %s", tokens[0], tokens[1]), // date
                    tokens[2], // time
                    tokens[3], // hostname
                    tokens[4].substring(0, tokens[4].length() - 1), // source (remove tailing colon)
                    tokens[5]); // message
            System.out.println(logMessage);

            String date = String.format("%s %s %s", tokens[0], tokens[1], tokens[2]);
            lastMinute.report(date);
            lastHour.report(date);
            lastDay.report(date);

            if (System.currentTimeMillis() - timer > 10000) {
                timer = System.currentTimeMillis();
                String currentTime = TimeStatistics.getCurrentDate();
                System.out.println("Number of logs in last minute: " + lastMinute.getSum(currentTime));
                System.out.println("Number of logs in last hour: " + lastHour.getSum(currentTime));
                System.out.println("Number of logs in last day: " + lastDay.getSum(currentTime));
            }

            KeyedMessage<Integer, LogMessage> msg = new KeyedMessage<>(topic, logMessage);
            producer.send(msg);
        }
    }
}
