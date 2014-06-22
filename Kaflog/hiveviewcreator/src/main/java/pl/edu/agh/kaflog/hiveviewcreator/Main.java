package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.utils.CloseableUtils;
import pl.edu.agh.kaflog.common.utils.ExecutorUtils;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


/**
 * Creates and runs bath job that imports LogMessages from HDFS to HiveTable
 */
public class Main implements ExecutorUtils.ThrowingRunnable {
    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.run();
    }

    @Override
    public void run() throws IOException, SQLException, InterruptedException {
        ExecutorUtils executorUtils = null;
        try {
            executorUtils = new ExecutorUtils();
            HiveDataImporter hiveDataImporter = new HiveDataImporter();
            Path root = new Path(KaflogProperties.getProperty("kaflog.hdfs.uri") +
                    KaflogProperties.getProperty("kaflog.hive.view.dataPath"));
            hiveDataImporter.fromHdfs(root, true);
            executorUtils.addTasks(hiveDataImporter.createViews());
        } finally {
            CloseableUtils.close(executorUtils);
        }


    }
}
