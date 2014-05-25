package pl.edu.agh.kaflog.stormconsumer.bolts;

import backtype.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;


public class FieldsKey {
    private final List<String> keys;
    private final List<Object> values;

    public FieldsKey(Tuple tuple, List<String> keys) {
        this.keys = keys;
        this.values = new ArrayList<Object>();
        for (String key : keys) {
            values.add(tuple.getValueByField(key));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldsKey fieldsKey = (FieldsKey) o;
        return fieldsKey.keys.equals(keys) && fieldsKey.values.equals(values);
    }

    @Override
    public int hashCode() {
        return 31 * keys.hashCode() + values.hashCode();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String key: keys) {
            sb.append("$").append(key);
        }
        return sb.toString();
    }
}
