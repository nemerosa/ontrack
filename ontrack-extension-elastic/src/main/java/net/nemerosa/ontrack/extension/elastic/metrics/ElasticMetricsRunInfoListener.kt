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
                ECSEntry(
                    timestamp = runnableEntity.runTime,
                    event = ECSEvent(
                        category = "run_info",
                        type = runnableEntity.runnableEntityType.name.lowercase(), // build or validation_run
                        duration = runTime * 1_000_000_000L, // To nanoseconds
                    ),
                    labels = runnableEntity.runMetricTags + ("name" to runnableEntity.runMetricName),
                )
            )
        }
    }
}