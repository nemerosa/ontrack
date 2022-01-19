package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.general.validation.MetricsValidationData
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.config
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@TestPropertySource(properties = [
    "ontrack.extension.elastic.metrics.enabled=true",
    "ontrack.extension.elastic.metrics.index.immediate=true",
])
class ElasticMetricsValidationRunMetricsExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var metricsValidationDataType: MetricsValidationDataType

    @Autowired
    private lateinit var elasticMetricsClient: ElasticMetricsClient

    @Test
    fun `Export of validation data metrics for generic metrics`() {
        val dataConfig = metricsValidationDataType.config(null)
        project {
            branch {
                // Typed validation
                val vs = validationStamp(validationDataTypeConfig = dataConfig)
                // Build
                build {
                    // Validation with data
                    validateWithData(vs, validationRunData = MetricsValidationData(
                        mapOf(
                            "position" to 10.0,
                            "speed" to 5.0,
                            "acceleration" to 0.5,
                        )
                    ))
                    // Checks the metric has been exported into ES
                    val results = elasticMetricsClient.rawSearch(
                        token = vs.name,
                        indexName = "ontrack_metric_validation_data",
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                }
            }
        }
    }

}