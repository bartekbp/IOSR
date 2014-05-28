package pl.edu.agh.kaflog.stormconsumer.bolts;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;

import java.io.Serializable;

public class HTDescriptor implements Serializable{
    final public String name;
    final public String[] columns;


    public HTDescriptor(String name, String... columns) {
        this.name = name;
        this.columns = columns;
    }

    public HTableDescriptor getHTableDescriptor() {
        HTableDescriptor descriptor = new HTableDescriptor(name);
        for (String columnFamily : columns) {
            descriptor.addFamily(new HColumnDescriptor(columnFamily));
        }
        return descriptor;
    }
}
