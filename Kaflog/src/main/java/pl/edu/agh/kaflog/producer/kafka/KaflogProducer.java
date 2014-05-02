package pl.edu.agh.kaflog.producer.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import pl.edu.agh.kaflog.producer.Main;

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.Properties;

/**
 * We could replace this class with:
 * https://github.com/xstevens/syslog-kafka/blob/master/src/main/java/kafka/syslog/SyslogKafkaServer.java
 * (in this way we could achieve better log parsing and higher level api)
 */
public class KaflogProducer implements Main.ThrowingRunnable {
    private Producer<Integer, String> producer;
    private Properties props = new Properties();

    private final TimeStatistics lastMinute = new TimeStatistics(1, 60);
    private final TimeStatistics lastHour = new TimeStatistics(60, 60);
    private final TimeStatistics lastDay = new TimeStatistics(3600, 24);

    public KaflogProducer() {
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        producer = new Producer<>(new ProducerConfig(props));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                producer.close();
            }
        });
    }

    public void run() throws IOException, ParseException {
        DatagramSocket socket = new DatagramSocket(5001, InetAddress.getByName("127.0.0.1"));
        System.out.println("Listen on " + socket.getLocalAddress() + " from " + socket.getInetAddress() + " port " + socket.getPort());
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        lastMinute.start(TimeStatistics.getCurrentDate());
        lastHour.start(TimeStatistics.getCurrentDate());
        lastDay.start(TimeStatistics.getCurrentDate());

        long timer = System.currentTimeMillis();
        while (true) {
            socket.receive(packet);
            // Data is in format:
            // <xxx>mmm dd hh:mm:ss hostname user: message
            String data = new String(packet.getData(), 0, packet.getLength());
            data = data.substring(data.indexOf('>') + 1);
            String[] tokens = data.split("\\s+", 6);
//            System.out.println(tokens[0]);
//            System.out.println(tokens[1]);
//            System.out.println(tokens[2]);
//            System.out.println(tokens[3]);
//            System.out.println(tokens[4]);
//            System.out.println(tokens[5]);
            System.out.println(data);

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

            if (tokens[5].equals("finish\n")) {
                socket.close();
                break;
            }

            KeyedMessage<Integer, String> msg = new KeyedMessage<>("kaflogtopic", data);
            producer.send(msg);
        }
    }
}
