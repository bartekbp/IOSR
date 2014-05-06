package pl.edu.agh.kaflog.master.monitoring;


import com.j256.simplejmx.server.JmxServer;
import pl.edu.agh.kaflog.producer.Main;

import javax.management.JMException;


public class MBeanPublisher {
    private final int port;
    private State state = State.Created;
    private JmxServer jmxServer;

    public static MBeanPublisher withPort(int port) {
        return new MBeanPublisher(port);
    }

    private MBeanPublisher(int port) {
        this.port = port;
    }

    public void startServer() throws JMException {
        state.onStartServer(this);

        jmxServer = new JmxServer(port);
        jmxServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                jmxServer.stop();
            }
        });
    }

    public void registerObject(Object object) throws JMException {
        state.onRegisterObject(this);
        jmxServer.register(object);
    }

    private enum State {
        Created  {
            @Override
            public void onStartServer(MBeanPublisher publisher) {
                publisher.state = State.Started;
            }

            @Override
            public void onRegisterObject(MBeanPublisher publisher) {
                throw new IllegalStateException("Server not started");
            }
        },
        Started {
            @Override
            public void onStartServer(MBeanPublisher publisher) {
                throw new IllegalStateException("Server already started");
            }
        };

        public void onStartServer(MBeanPublisher publisher) {}
        public void onRegisterObject(MBeanPublisher publisher) {}
    }
}
