package net.nemerosa.ontrack.extension.metrics.influxdb.client;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * This class is a bean that holds time series data of a point. A point co relates to a metric.
 */
@Data
public class InfluxDbPoint {

    private final String measurement;
    private final Map<String, String> tags;
    private final long timestamp;
    private final Map<String, Object> fields;

    public InfluxDbPoint(String measurement, Map<String, String> tags, long timestamp, Map<String, Object> fields) {
        this.measurement = measurement;
        if (tags != null) {
            this.tags = Collections.unmodifiableMap(tags);
        } else {
            this.tags = Collections.emptyMap();
        }
        this.timestamp = timestamp;
        if (fields != null) {
            this.fields = Collections.unmodifiableMap(fields);
        } else {
            this.fields = Collections.emptyMap();
        }
    }

}
