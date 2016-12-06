package net.nemerosa.ontrack.model.metrics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.actuate.metrics.Metric;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public final class TaggedMetric<T extends Number> extends Metric<T> {

    private final Map<String, String> tags;

    public TaggedMetric(String name, T value, Date timestamp, Map<String, String> tags) {
        super(name, value, timestamp);
        this.tags = tags;
    }

    public static <T extends Number> TaggedMetricBuilder<T> of(String name, T value) {
        return new TaggedMetricBuilder<>(name, value);
    }

    @Data
    public static class TaggedMetricBuilder<T extends Number> {
        private final String name;
        private final Date time;
        private final T value;
        private final Map<String, String> tags = new LinkedHashMap<>();

        public TaggedMetricBuilder(String name, T value) {
            this.name = name;
            this.value = value;
            this.time = new Date();
        }

        public TaggedMetricBuilder<T> tag(String key, String value) {
            tags.put(key, value);
            return this;
        }

        public TaggedMetric<T> build() {
            return new TaggedMetric<>(
                    name,
                    value,
                    time,
                    Collections.unmodifiableMap(tags)
            );
        }

    }

}
