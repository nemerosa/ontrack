package net.nemerosa.ontrack.service.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import net.nemerosa.ontrack.repository.StatsRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class EntityCountMetrics(
        private val repository: StatsRepository
) : MeterBinder {

    private fun MeterRegistry.count(
            name: String,
            countFn: StatsRepository.() -> Int
    ) {
        gauge(
                "ontrack.entity.$name",
                repository,
                { repository.countFn().toDouble() }
        )
    }

    override fun bindTo(registry: MeterRegistry) {
        registry.count("project", { projectCount })
        registry.count("branch", { branchCount })
        registry.count("build", { buildCount })
        registry.count("promotionLevel", { promotionLevelCount })
        registry.count("promotionRun", { promotionRunCount })
        registry.count("validationStamp", { validationStampCount })
        registry.count("validationRun", { validationRunCount })
        registry.count("validationRunStatus", { validationRunStatusCount })
        registry.count("property", { propertyCount })
        registry.count("event", { eventCount })
    }

}
