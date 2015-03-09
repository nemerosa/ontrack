package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

@Component
public class EntityCountMetrics implements PublicMetrics {

    private final StatsRepository repository;

    @Autowired
    public EntityCountMetrics(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Metric<?>> metrics() {
        return Arrays.asList(
                new Metric<>("counter.entity.project", repository.getProjectCount()),
                new Metric<>("counter.entity.branch", repository.getBranchCount())
        );
    }

}
