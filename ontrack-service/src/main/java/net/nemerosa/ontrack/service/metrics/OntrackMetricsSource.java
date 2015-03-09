package net.nemerosa.ontrack.service.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Export of all {@link net.nemerosa.ontrack.service.metrics.OntrackMetrics}.
 */
@Component
public class OntrackMetricsSource implements MetricsSource {

    private final Collection<OntrackMetrics> ontrackMetrics;

    @Autowired
    public OntrackMetricsSource(Collection<OntrackMetrics> ontrackMetrics) {
        this.ontrackMetrics = ontrackMetrics;
    }

    @Override
    public Stream<Metric<?>> getMetrics() {
        return ontrackMetrics.stream().flatMap(p -> p.metrics().stream());
    }

}
