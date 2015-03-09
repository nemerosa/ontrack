package net.nemerosa.ontrack.service.metrics;

import org.springframework.boot.actuate.metrics.Metric;

import java.util.stream.Stream;

public interface MetricsSource {

    Stream<Metric<?>> getMetrics();

}
