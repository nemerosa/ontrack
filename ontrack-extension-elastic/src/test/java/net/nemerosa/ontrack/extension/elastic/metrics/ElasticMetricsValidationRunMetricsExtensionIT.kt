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
import kotlin.test.assertNull

@TestPropertySource(
    properties = [
        "ontrack.extension.elastic.metrics.enabled=true",
        "ontrack.extension.elastic.metrics.index.immediate=true",
    ]
)
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
                    validateWithData(
                        vs, validationRunData = MetricsValidationData(
                            mapOf(
                                "position" to 10.0,
                                "speed" to 5.0,
                                "acceleration" to 0.5,
                            )
                        )
                    )
                    // Checks the metric has been exported into ES
                    val results = elasticMetricsClient.rawSearch(
                        token = vs.name,
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val ecs = result.source.asJson().parse<ECSEntry>()

                    assertEquals(
                        mapOf(
                            "acceleration" to 0.5,
                            "position" to 10.0,
                            "speed" to 5.0,
                        ),
                        ecs.ontrack
                    )

                    assertEquals(
                        mapOf(
                            "project" to project.name,
                            "branch" to branch.name,
                            "type" to "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                            "validation" to vs.name,
                            "status" to "PASSED",
                        ),
                        ecs.labels
                    )

                    assertNull(ecs.tags)

                    assertEquals(
                        ECSEvent(
                            kind = ECSEventKind.metric,
                            category = "validation_data",
                        ),
                        ecs.event
                    )
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
                    validateWithData(
                        vs, validationRunData = TestSummaryValidationData(
                            passed = 13,
                            skipped = 8,
                            failed = 5,
                        )
                    )
                    // Checks the metric has been exported into ES
                    val results = elasticMetricsClient.rawSearch(
                        token = vs.name,
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val ecs = result.source.asJson().parse<ECSEntry>()

                    assertEquals(
                        mapOf(
                            "total" to 26,
                            "passed" to 13,
                            "failed" to 5,
                            "skipped" to 8
                        ),
                        ecs.ontrack
                    )

                    assertEquals(
                        mapOf(
                            "project" to project.name,
                            "branch" to branch.name,
                            "type" to "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
                            "validation" to vs.name,
                            "status" to "FAILED",
                        ),
                        ecs.labels
                    )

                    assertNull(ecs.tags)

                    assertEquals(
                        ECSEvent(
                            kind = ECSEventKind.metric,
                            category = "validation_data",
                        ),
                        ecs.event
                    )
                }
            }
        }
    }

}