package pl.edu.agh.kaflog.hadoopconsumer;

import com.google.common.collect.ObjectArrays;
import com.linkedin.camus.etl.kafka.CamusJob;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private final String[] args;

    public Main(String[] args) {
        this.args = args;
    }

    public static void main(String... args) throws Exception {
        Main main = new Main(ObjectArrays.concat(args, new String[]{"-p", "camus.properties"}, String.class));
        main.run();
    }

    @Override
    public void run() {
        try {
            CamusJob.main(args);
        } catch (Exception e) {
            LOG.error("", e.getStackTrace());
        }
    }
}
