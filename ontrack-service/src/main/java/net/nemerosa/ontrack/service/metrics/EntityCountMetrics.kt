package net.nemerosa.ontrack.service.metrics

import net.nemerosa.ontrack.model.metrics.OntrackMetrics
import net.nemerosa.ontrack.repository.StatsRepository
import org.springframework.boot.actuate.metrics.Metric
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class EntityCountMetrics(
        private val repository: StatsRepository
) : OntrackMetrics {

    @Transactional(readOnly = true)
    override fun metrics(): Collection<Metric<*>> {
        return Arrays.asList<Metric<*>>(
                Metric("gauge.entity.project", repository.projectCount),
                Metric("gauge.entity.branch", repository.branchCount),
                Metric("gauge.entity.build", repository.buildCount),
                Metric("gauge.entity.promotionLevel", repository.promotionLevelCount),
                Metric("gauge.entity.promotionRun", repository.promotionRunCount),
                Metric("gauge.entity.validationStamp", repository.validationStampCount),
                Metric("gauge.entity.validationRun", repository.validationRunCount),
                Metric("gauge.entity.validationRunStatus", repository.validationRunStatusCount),
                Metric("gauge.entity.property", repository.propertyCount),
                Metric("gauge.entity.event", repository.eventCount)
        )
    }

}
