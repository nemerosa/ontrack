package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoListener
import net.nemerosa.ontrack.model.structure.RunnableEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(ElasticMetricsClient::class)
class ElasticMetricsRunInfoListener(
    private val elasticMetricsClient: ElasticMetricsClient,
) : RunInfoListener {
    override fun onRunInfoCreated(runnableEntity: RunnableEntity, runInfo: RunInfo) {
        val runTime = runInfo.runTime
        if (runTime != null) {
            elasticMetricsClient.saveMetric(
                metric = "run_info_${runnableEntity.runnableEntityType.name.lowercase()}",
                data = mapOf(
                    "tags" to runnableEntity.runMetricTags,
                    "fields" to mapOf(
                        "value" to runTime,
                        "name" to runnableEntity.runMetricName,
                    ),
                    "timestamp" to runnableEntity.runTime,
                )
            )
        }
    }
}