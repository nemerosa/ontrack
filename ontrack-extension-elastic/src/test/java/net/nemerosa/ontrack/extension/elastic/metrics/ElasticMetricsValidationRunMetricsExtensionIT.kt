package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.general.validation.*
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
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
    private lateinit var testSummaryValidationDataType: TestSummaryValidationDataType

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
                    val result = results.items.first()
                    val source = result.source.asJson()
                    assertEquals(10.0, source["fields"]["position"].asDouble())
                    assertEquals(5.0, source["fields"]["speed"].asDouble())
                    assertEquals(0.5, source["fields"]["acceleration"].asDouble())
                }
            }
        }
    }

    @Test
    fun `Export of validation data metrics for test metrics`() {
        val dataConfig = testSummaryValidationDataType.config(
            TestSummaryValidationConfig(
                warningIfSkipped = true,
            )
        )
        project {
            branch {
                // Typed validation
                val vs = validationStamp(validationDataTypeConfig = dataConfig)
                // Build
                build {
                    // Validation with data
                    validateWithData(vs, validationRunData = TestSummaryValidationData(
                        passed = 13,
                        skipped = 8,
                        failed = 5,
                    ))
                    // Checks the metric has been exported into ES
                    val results = elasticMetricsClient.rawSearch(
                        token = vs.name,
                        indexName = "ontrack_metric_validation_data",
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val source = result.source.asJson()
                    assertEquals(13.0, source["fields"]["passed"].asDouble())
                    assertEquals(8.0, source["fields"]["skipped"].asDouble())
                    assertEquals(5.0, source["fields"]["failed"].asDouble())
                }
            }
        }
    }

}