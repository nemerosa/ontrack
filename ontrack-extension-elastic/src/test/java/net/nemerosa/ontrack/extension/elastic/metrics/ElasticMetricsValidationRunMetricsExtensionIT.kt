package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.general.validation.*
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
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
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val source = result.source.asJson().parse<ECSEntry>()

                    assertEquals("validation_data", source.event.category)
                    assertEquals(ECSEventOutcome.success, source.event.outcome)

                    assertEquals(project.name, source.labels?.get("project"))
                    assertEquals(branch.name, source.labels?.get("branch"))
                    assertEquals(name, source.labels?.get("build"))
                    assertEquals(vs.name, source.labels?.get("validation"))
                    assertEquals("PASSED", source.labels?.get("status"))
                    assertEquals(metricsValidationDataType::class.java.name, source.labels?.get("type"))

                    assertEquals(10.0, source.ontrack?.get("position"))
                    assertEquals(5.0, source.ontrack?.get("speed"))
                    assertEquals(0.5, source.ontrack?.get("acceleration"))
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
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val source = result.source.asJson().parse<ECSEntry>()

                    assertEquals("validation_data", source.event.category)
                    assertEquals(ECSEventOutcome.failure, source.event.outcome)

                    assertEquals(project.name, source.labels?.get("project"))
                    assertEquals(branch.name, source.labels?.get("branch"))
                    assertEquals(name, source.labels?.get("build"))
                    assertEquals(vs.name, source.labels?.get("validation"))
                    assertEquals("FAILED", source.labels?.get("status"))
                    assertEquals(testSummaryValidationDataType::class.java.name, source.labels?.get("type"))

                    assertEquals(13, source.ontrack?.get("passed"))
                    assertEquals(8, source.ontrack?.get("skipped"))
                    assertEquals(5, source.ontrack?.get("failed"))
                }
            }
        }
    }

}