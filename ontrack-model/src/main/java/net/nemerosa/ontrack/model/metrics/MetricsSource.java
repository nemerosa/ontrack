package net.nemerosa.ontrack.model.metrics;

import org.springframework.boot.actuate.metrics.Metric;

import java.util.stream.Stream;

public interface MetricsSource {

    Stream<Metric<?>> getMetrics();

}
