package net.nemerosa.ontrack.service.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Export all Spring Boot metrics which are neither counters nor gauges. This makes
 * accessible the list of metrics like <code>gc</code>, <code>mem</code>, etc.
 */
@Component
public class SystemMetricsSource implements MetricsSource {

    private final Collection<PublicMetrics> publicMetrics;

    @Autowired
    public SystemMetricsSource(Collection<PublicMetrics> publicMetrics) {
        this.publicMetrics = publicMetrics;
    }

    @Override
    public Stream<Metric<?>> getMetrics() {
        return publicMetrics.stream()
                .flatMap(m -> m.metrics().stream())
                .filter(m -> !m.getName().startsWith("counter"))
                .filter(m -> !m.getName().startsWith("gauge"))
                ;
    }

}
