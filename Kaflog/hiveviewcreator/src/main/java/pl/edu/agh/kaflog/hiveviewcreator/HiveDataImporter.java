package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import pl.edu.agh.kaflog.common.utils.CloseableUtils;
import pl.edu.agh.kaflog.common.utils.HdfsUtils;
import pl.edu.agh.kaflog.common.utils.IteratorUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

public class HiveDataImporter {

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

            hdfsUtils.deleteSubPaths(root);
        } finally {
            CloseableUtils.close(hiveDao);
            CloseableUtils.close(hdfsUtils);
        }
    }
}
