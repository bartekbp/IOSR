package pl.edu.agh.kaflog.hiveviewcreator.util;


import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;


/**
 * Encapsulate hdfs related operations
 */
public class HdfsUtils implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(HdfsUtils.class);
    private FileSystem fileSystem;

    /**
     * Initialize HdfsUtils to use proper hdfs (defined in kaflog.properties)
     */
    public HdfsUtils() {
        Configuration configuration = new Configuration();
        configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        try {
            fileSystem = FileSystem.get(new URI(KaflogProperties.getProperty("kaflog.hdfs.uri")), configuration);
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * List files in configured hdfs system
     * @param root path to be listed
     * @param recursive if true then also subdirectories would be listed
     * @return list of Paths
     * @throws IOException
     */
    public Iterator<Path> listFiles(Path root, boolean recursive) throws IOException {
        return  Iterators.transform(IteratorUtils.fromRemoteIterator(fileSystem.listFiles(root, recursive)), new Function<LocatedFileStatus, Path>() {
            @Override
            public Path apply(LocatedFileStatus status) {
                return status.getPath();
            }
        });
    }

    /**
     * Deletes dir
     * @param path of fir to delete
     * @throws IOException
     */
    public void deleteDir(Path path) throws IOException {
        fileSystem.delete(path, true);
    }

    /**
     * Creates dir
     * @param path of dir to be created
     * @throws IOException
     */
    public void createDir(Path path) throws IOException {
        fileSystem.create(path);
    }

    /**
     * Disconnects form hdsf
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        fileSystem.close();
    }


    /**
     * Delete subdirectories - it clears directory with given path
     * @param path
     * @throws IOException
     */
    public void deleteSubPaths(Path path) throws IOException {
        for(Path p : FileUtil.stat2Paths(fileSystem.listStatus(path))) {
            fileSystem.delete(p, true);
        }
    }
}
