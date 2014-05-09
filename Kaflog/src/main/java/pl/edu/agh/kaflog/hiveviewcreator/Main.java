package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.utils.ExecutorUtils;

import java.io.IOException;
import java.sql.SQLException;

public class Main implements ExecutorUtils.ThrowingRunnable {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        Main main = new Main();
        ExecutorUtils executorUtils = new ExecutorUtils();
        executorUtils.addTask(main);
    }

    @Override
    public void run() throws IOException, SQLException {
        HiveDataImporter hiveDataImporter = new HiveDataImporter();
        hiveDataImporter.fromHdfs(new Path("hdfs://cloudera-master:8020/user/kafka/output"), true);
    }
}