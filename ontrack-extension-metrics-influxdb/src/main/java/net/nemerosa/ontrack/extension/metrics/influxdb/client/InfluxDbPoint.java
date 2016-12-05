package net.nemerosa.ontrack.extension.metrics.influxdb.client;

import java.util.Collections;
import java.util.Map;

/**
 * This class is a bean that holds time series data of a point. A point co relates to a metric.
 */
public class InfluxDbPoint {
    private String measurement;
    private Map<String, String> tags = Collections.emptyMap();
    private long timestamp;
    private Map<String, Object> fields = Collections.emptyMap();

    public InfluxDbPoint(final String measurement, final long timestamp, final Map<String, Object> fields) {
        this.measurement = measurement;
        this.timestamp = timestamp;
        if (fields != null) {
            this.fields = Collections.unmodifiableMap(fields);
        }

    }

    public InfluxDbPoint(String measurement, Map<String, String> tags, long timestamp, Map<String, Object> fields) {
        this.measurement = measurement;
        if (tags != null) {
            this.tags = Collections.unmodifiableMap(tags);
        }
        this.timestamp = timestamp;
        if (fields != null) {
            this.fields = Collections.unmodifiableMap(fields);
        }
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        if (tags != null) {
            this.tags = Collections.unmodifiableMap(tags);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        if (fields != null) {
            this.fields = Collections.unmodifiableMap(fields);
        }
    }
}
