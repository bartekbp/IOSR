package pl.edu.agh.kaflog.hiveviewcreator;

import org.apache.hadoop.fs.Path;
import pl.edu.agh.kaflog.utils.HdfsUtils;
import pl.edu.agh.kaflog.utils.IteratorUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

public class HiveDataImporter {

    public void fromHdfs(Path root, boolean recursive) throws IOException, SQLException {

        try(HiveLogMessageDao hiveDao = new HiveLogMessageDao();
            HdfsUtils hdfsUtils = new HdfsUtils()) {

            Iterator<Path> paths = hdfsUtils.listFiles(root, recursive);
            hiveDao.createTableIfNotExistsWithFieldsDelimiter('\7');
            for(Path path : IteratorUtils.toIterable(paths)) {
                hiveDao.loadHdfs(path);
            }

            hdfsUtils.deleteSubPaths(root);
        }
    }
}
