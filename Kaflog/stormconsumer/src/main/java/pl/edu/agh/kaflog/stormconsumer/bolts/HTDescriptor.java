package pl.edu.agh.kaflog.stormconsumer.bolts;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;

import java.io.Serializable;

/**
 * Class encapsulate HBase table name and columns
 * That is needed for HBase bolt. It is introduced because {@link org.apache.hadoop.hbase.HTableDescriptor}
 * is not serializable
 */
public class HTDescriptor implements Serializable{
    final public String name;
    final public String[] columns;


    /**
     * @param name table name
     * @param columns column families name
     */
    public HTDescriptor(String name, String... columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     * Create HBase native {@link org.apache.hadoop.hbase.HTableDescriptor}
     * @return
     */
    public HTableDescriptor getHTableDescriptor() {
        HTableDescriptor descriptor = new HTableDescriptor(name);
        for (String columnFamily : columns) {
            descriptor.addFamily(new HColumnDescriptor(columnFamily));
        }
        return descriptor;
    }
}
