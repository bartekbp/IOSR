package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.KaflogConstants;
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

/**
 *  This class use HiveDao to implement all interactions with Hive
 */
public class HiveDataImporter {
    protected static final Logger LOG = LoggerFactory.getLogger(HiveDataImporter.class);

    /**
     * Loads data from HDFS to hive
     * @param root HDFS path - should point to place where LogMessages are stored
     * @param recursive - if true path would be searched recursive
     * @throws IOException
     * @throws SQLException
     */
    public void fromHdfs(Path root, boolean recursive) throws IOException, SQLException {

        HiveLogMessageDao hiveDao = null;
        HdfsUtils hdfsUtils = null;
        try {
            hiveDao = new HiveLogMessageDao();

            hdfsUtils = new HdfsUtils();

            Iterator<Path> paths = hdfsUtils.listFiles(root, recursive);
            hiveDao.createTableIfNotExistsWithFieldsDelimiter(KaflogConstants.SEPARATOR);
            for(Path path : IteratorUtils.toIterable(paths)) {
                hiveDao.loadHdfs(path);
            }

            LOG.debug("Data from hdfs loaded");
            hdfsUtils.deleteSubPaths(root);
        } finally {
            CloseableUtils.close(hiveDao);
            CloseableUtils.close(hdfsUtils);
        }
    }

    /**
     * Defines all needed tables if needed.
     * I.e.
     * <ul>
     *     <li>hdp_host_severity_per_time - table for hadoop results - batch layer</li>
     *     <li>hbase_srm_host_severity_per_minute - table for storm results -speed layer.
     *     This is external table actually stored in HBase</li>
     * </ul>
     * @return
     * @throws SQLException
     */
    public List<ExecutorUtils.ThrowingRunnable> createViews() throws SQLException {
        return Arrays.asList(new ExecutorUtils.ThrowingRunnable() {

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
                    LOG.debug("Created extranal hbase strom tables");
                } finally {
                    CloseableUtils.close(hiveDao);
                }
            }
        });
    }

}
