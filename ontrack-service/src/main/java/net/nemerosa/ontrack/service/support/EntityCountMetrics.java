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
                new Metric<>("counter.entity.branch", repository.getBranchCount()),
                new Metric<>("counter.entity.build", repository.getBuildCount()),
                new Metric<>("counter.entity.promotionLevel", repository.getPromotionLevelCount()),
                new Metric<>("counter.entity.promotionRun", repository.getPromotionRunCount()),
                new Metric<>("counter.entity.validationStamp", repository.getValidationStampCount()),
                new Metric<>("counter.entity.validationRun", repository.getValidationRunCount()),
                new Metric<>("counter.entity.validationRunStatus", repository.getValidationRunStatusCount()),
                new Metric<>("counter.entity.property", repository.getPropertyCount()),
                new Metric<>("counter.entity.event", repository.getEventCount())
        );
    }

}
