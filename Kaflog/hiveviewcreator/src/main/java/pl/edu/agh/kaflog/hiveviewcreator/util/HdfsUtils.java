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

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

public class HdfsUtils implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(HdfsUtils.class);
    private FileSystem fileSystem;

    public HdfsUtils() {
        Configuration configuration = new Configuration();
        configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        try {
            fileSystem = FileSystem.get(new URI("hdfs://cloudera-master:8020/"), configuration);
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    public Iterator<Path> listFiles(Path root, boolean recursive) throws IOException {
        return  Iterators.transform(IteratorUtils.fromRemoteIterator(fileSystem.listFiles(root, recursive)), new Function<LocatedFileStatus, Path>() {
            @Override
            public Path apply(LocatedFileStatus status) {
                return status.getPath();
            }
        });
    }

    public void deleteDir(Path path) throws IOException {
        fileSystem.delete(path, true);
    }

    public void createDir(Path path) throws IOException {
        fileSystem.create(path);
    }

    @Override
    public void close() throws IOException {
        fileSystem.close();
    }


    public void deleteSubPaths(Path path) throws IOException {
        for(Path p : FileUtil.stat2Paths(fileSystem.listStatus(path))) {
            fileSystem.delete(p, true);
        }
    }
}
