package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.api.ValidationRunMetricsExtension
import net.nemerosa.ontrack.extension.elastic.ElasticExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunData
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(ElasticMetricsClient::class)
class ElasticMetricsValidationRunMetricsExtension(
    elasticExtensionFeature: ElasticExtensionFeature,
    private val elasticMetricsClient: ElasticMetricsClient,
    private val validationDataTypeService: ValidationDataTypeService,
) : AbstractExtension(elasticExtensionFeature), ValidationRunMetricsExtension {

    override fun onValidationRun(validationRun: ValidationRun) {
        val validationRunData: ValidationRunData<*>? = validationRun.data
        if (validationRunData != null) {
            onValidationRunData(validationRun, validationRunData)
        }
    }

    private fun <T> onValidationRunData(
        validationRun: ValidationRun,
        validationRunData: ValidationRunData<T>,
    ) {
        val dataType: ValidationDataType<Any, T>? =
            validationDataTypeService.getValidationDataType(validationRunData.descriptor.id)
        if (dataType != null) {
            val metrics: Map<String, *>? = dataType.getMetrics(validationRunData.data)
            if (metrics != null && metrics.isNotEmpty()) {
                elasticMetricsClient.saveMetric(
                    ECSEntry(
                        timestamp = validationRun.signature.time,
                        event = ECSEvent(
                            category = "validation_data",
                            outcome = if (validationRun.lastStatus.isPassed) {
                                ECSEventOutcome.success
                            } else {
                                ECSEventOutcome.failure
                            },
                        ),
                        labels = mapOf(
                            "project" to validationRun.project.name,
                            "branch" to validationRun.validationStamp.branch.name,
                            "build" to validationRun.build.name,
                            "validation" to validationRun.validationStamp.name,
                            "status" to validationRun.lastStatus.statusID.id,
                            "type" to validationRunData.descriptor.id,
                        ),
                        ontrack = metrics,
                    )
                )
            }
        }
    }
}