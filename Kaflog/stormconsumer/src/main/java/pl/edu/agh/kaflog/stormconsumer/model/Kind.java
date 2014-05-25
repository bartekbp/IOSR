package pl.edu.agh.kaflog.stormconsumer.model;

public enum Kind {
    CREATED(0L),
    REVOKING_MINUTE(60000L),
    REVOKING_HOUR(3600000L),
    REVOKING_DAY(86400000L);

    private final long interval;

    private Kind(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }
}