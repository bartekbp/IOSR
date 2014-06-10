package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.utils.CloseableUtils;
import pl.edu.agh.kaflog.common.utils.ExecutorUtils;
import pl.edu.agh.kaflog.hiveviewcreator.dao.HiveHBaseDao;
import pl.edu.agh.kaflog.hiveviewcreator.dao.HiveLogMessageDao;
import pl.edu.agh.kaflog.hiveviewcreator.util.HdfsUtils;
import pl.edu.agh.kaflog.hiveviewcreator.util.IteratorUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HiveDataImporter {
    protected static final Logger LOG = LoggerFactory.getLogger(HiveDataImporter.class);

    public void fromHdfs(Path root, boolean recursive) throws IOException, SQLException {

        HiveLogMessageDao hiveDao = null;
        HdfsUtils hdfsUtils = null;
        try {
            hiveDao = new HiveLogMessageDao();

            hdfsUtils = new HdfsUtils();

            Iterator<Path> paths = hdfsUtils.listFiles(root, recursive);
            hiveDao.createTableIfNotExistsWithFieldsDelimiter('\7');
            for(Path path : IteratorUtils.toIterable(paths)) {
                hiveDao.loadHdfs(path);
            }

            LOG.debug("Data to hdfs loaded");
            hdfsUtils.deleteSubPaths(root);
        } finally {
            CloseableUtils.close(hiveDao);
            CloseableUtils.close(hdfsUtils);
        }
    }

    public List<ExecutorUtils.ThrowingRunnable> createViews() throws SQLException {
        return Arrays.asList(new ExecutorUtils.ThrowingRunnable() {
            public void run() throws Exception {
                HiveHBaseDao hiveDao = null;

                try {
                    hiveDao = new HiveHBaseDao();
                    hiveDao.createSeverityPerTimeView();
                    LOG.debug("Created severity per time view");
                } finally {
                    CloseableUtils.close(hiveDao);
                }
            }
        }, new ExecutorUtils.ThrowingRunnable() {

            @Override
            public void run() throws Exception {
                HiveHBaseDao hiveDao = null;

                try {
                    hiveDao = new HiveHBaseDao();
                    hiveDao.createHostPerTimeView();
                    LOG.debug("Created host per time view");
                } finally {
                    CloseableUtils.close(hiveDao);
                }
            }
        }, new ExecutorUtils.ThrowingRunnable() {

            @Override
            public void run() throws Exception {
                HiveHBaseDao hiveDao = null;

                try {
                    hiveDao = new HiveHBaseDao();
                    hiveDao.createHostPerSeverityPerTimeView();
                    LOG.debug("Created host per severity per time view");
                } finally {
                    CloseableUtils.close(hiveDao);
                }
            }
        }, new ExecutorUtils.ThrowingRunnable() {
            @Override
            public void run() throws Exception {
                HiveHBaseDao hiveDao = null;

                try {
                    hiveDao = new HiveHBaseDao();
                    hiveDao.ensureStromTablesAvailability();
                    LOG.debug("Created access to hbase strom tables ensured");
                } finally {
                    CloseableUtils.close(hiveDao);
                }
            }
        });
    }

}
