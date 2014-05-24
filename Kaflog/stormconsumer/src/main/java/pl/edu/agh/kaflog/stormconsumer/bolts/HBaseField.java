package pl.edu.agh.kaflog.stormconsumer.bolts;

public class HBaseField {
    public final String family, qualifier, value;

    public HBaseField(String family, String qualifier, String value) {
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return family + ": " + qualifier + ": " + value;
    }
}
