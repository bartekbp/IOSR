package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.utils.ExecutorUtils;

import java.io.IOException;
import java.sql.SQLException;

public class Main implements ExecutorUtils.ThrowingRunnable {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.run();
    }

    @Override
    public void run() throws IOException, SQLException {
        HiveDataImporter hiveDataImporter = new HiveDataImporter();
        hiveDataImporter.fromHdfs(new Path("hdfs://cloudera-master:8020/user/vagrant/kafka/output"), true);
    }
}