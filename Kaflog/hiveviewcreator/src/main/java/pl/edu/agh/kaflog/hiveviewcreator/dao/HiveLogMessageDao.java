package pl.edu.agh.kaflog.hiveviewcreator.dao;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.joda.time.DateTime;
import pl.edu.agh.kaflog.common.LogMessage;
import pl.edu.agh.kaflog.common.utils.KaflogProperties;

import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Random;

public class HiveLogMessageDao extends AbstractHiveDao {
    private final String hiveTable = KaflogProperties.getProperty("kaflog.hive.table");

    public HiveLogMessageDao() throws SQLException {

    }

    public HiveLogMessageDao dropTable() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeUpdate("drop table " + hiveTable);
            }
        });
        return this;
    }

    public HiveLogMessageDao createTableIfNotExistsWithFieldsDelimiter(final char delimiter) throws SQLException {
        insertRandomData();
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeUpdate("create table IF NOT EXISTS " + hiveTable +
                        " (severity string, time bigint, hostname string, source string, message string) " +
                        "ROW FORMAT DELIMITED  FIELDS TERMINATED BY '" + delimiter + "'");
            }
        });
        return this;
    }

    public void insertRandomData() throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                Configuration configuration = new Configuration();
                configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
                Path f = new Path("/user/vagrant/test");
                try {
                    FileSystem fileSystem = FileSystem.get(new URI("hdfs://cloudera-master:8020/"), configuration);
                    FSDataOutputStream out;
                    if(!fileSystem.exists(f)) {
                        out = FileSystem.create(fileSystem, f, FsPermission.createImmutable((short) 0777));
                    } else {
                        out = fileSystem.append(f);
                    }
                    DateTime dateTime = new DateTime();
                    DateTime dateTime_before = new DateTime().minusDays(15);
                    int[] host_prob = new int[9];
                    Random rand = new Random();
                    for(int i = 0; i < 9; i++) {
                        host_prob[i] = (int) (2.0 + rand.nextDouble() * 7);
                    }

                    String hostname = "kafka-node";
                    String s = Character.toString('\7') + "\n";
                    for(long i = 0; i < 1000000; i++) {
                        int current_host = rand.nextInt(9);
                        String rand_host = hostname + String.valueOf(current_host + 1);
                        int level;
                        if (i % 1231 == 0) {
                            level = 2;
                        } else if (i % 731 == 0) {
                            level = 3;
                        } else if (i % 527 == 0) {
                            level = 4;
                        } else if (i % 371 == 0) {
                            level = 5;
                        } else if (i % 71 == 0) {
                            level = 6;
                        } else {
                            level = 7;
                        }

                        for (int j = 0; j < host_prob[current_host]; j++) {
                            long rand_timestamp = (dateTime_before.getMillis() + (rand.nextLong() % (dateTime.getMillis() - dateTime_before.getMillis())));
                            LogMessage logMessage = new LogMessage(0, level, rand_timestamp, rand_host, "vagrant", "Auto generated log " + (i * 10 + j));
                            out.writeBytes((logMessage.toHdfsFormat() + s));
                        }
                        System.out.println(i);
                    }
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadHdfs(f);
                return null;
            }
        });
    }

    public List<String> describeTable() throws SQLException {
        return withResultSet(new CallableResultSet<List<String>>() {
            @Override
            public String query() {
                return "describe " + hiveTable;
            }

            @Override
            public List<String> call(ResultSet resultSet) throws SQLException {
                RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(resultSet);
                return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, java.lang.String>() {
                    @Override
                    public java.lang.String apply(DynaBean bean) {
                        return String.format("col_name=%s data_type=%s, comment=%s",
                                bean.get("col_name"),
                                bean.get("data_type"),
                                bean.get("comment"));
                    }
                });
            }
        });
    }

    public HiveLogMessageDao load(final Path path) throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.execute("load data local inpath '" + path + "' into table " + hiveTable);
            }
        });

        return this;
    }

    public HiveLogMessageDao loadHdfs(final Path path) throws SQLException {
        withStatement(new CallableStatement<Object>() {
            @Override
            public Object call(Statement statement) throws SQLException {
                return statement.executeUpdate("load data inpath '" + path + "' into table " + hiveTable);
            }
        });

        return this;
    }

    public List<LogMessage> selectAll() throws SQLException {
        return withResultSet(new CallableResultSet<List<LogMessage>>() {
            @Override
            public String query() {
                return "select * from " + hiveTable + " where `time` is not null";
            }

            @Override
            public List<LogMessage> call(ResultSet resultSet) throws SQLException {
                RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(resultSet);
                return Lists.transform(rowSetDynaClass.getRows(), new Function<DynaBean, LogMessage>() {
                    @Override
                    public LogMessage apply(DynaBean dynaBean) {
                        return LogMessage.fromHive(Joiner.on('\07').join(ObjectUtils.defaultIfNull(dynaBean.get("severity"), LogMessage.LEVEL_STRING[LogMessage.LEVEL_STRING.length - 1]),
                                ObjectUtils.defaultIfNull(dynaBean.get("time"), ""),
                                ObjectUtils.defaultIfNull(dynaBean.get("hostname"), ""),
                                ObjectUtils.defaultIfNull(dynaBean.get("source"), ""),
                                ObjectUtils.defaultIfNull(dynaBean.get("message"), "")));
                    }
                });
            }
        });
    }
}
