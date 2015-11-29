package net.nemerosa.ontrack.service.metrics;

import net.nemerosa.ontrack.model.metrics.OntrackMetrics;
import net.nemerosa.ontrack.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

@Component
public class EntityCountMetrics implements OntrackMetrics {

    private final StatsRepository repository;

    @Autowired
    public EntityCountMetrics(StatsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Metric<?>> metrics() {
        return Arrays.asList(
                new Metric<>("gauge.entity.project", repository.getProjectCount()),
                new Metric<>("gauge.entity.branch", repository.getBranchCount()),
                new Metric<>("gauge.entity.build", repository.getBuildCount()),
                new Metric<>("gauge.entity.promotionLevel", repository.getPromotionLevelCount()),
                new Metric<>("gauge.entity.promotionRun", repository.getPromotionRunCount()),
                new Metric<>("gauge.entity.validationStamp", repository.getValidationStampCount()),
                new Metric<>("gauge.entity.validationRun", repository.getValidationRunCount()),
                new Metric<>("gauge.entity.validationRunStatus", repository.getValidationRunStatusCount()),
                new Metric<>("gauge.entity.property", repository.getPropertyCount()),
                new Metric<>("gauge.entity.event", repository.getEventCount())
        );
    }

}
