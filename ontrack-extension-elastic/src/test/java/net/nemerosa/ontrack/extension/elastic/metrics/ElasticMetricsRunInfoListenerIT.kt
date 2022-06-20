package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.RunInfoInput
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNull

@TestPropertySource(
    properties = [
        "ontrack.extension.elastic.metrics.enabled=true",
        "ontrack.extension.elastic.metrics.index.immediate=true",
        "management.metrics.export.elastic.enabled=false",
    ]
)
class ElasticMetricsRunInfoListenerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var elasticMetricsClient: ElasticMetricsClient

    @Test
    fun `Export of run info for a build`() {
        project {
            branch {
                build {
                    runInfoService.setRunInfo(
                        this, RunInfoInput(
                            runTime = 50,
                        )
                    )
                    // Checks the metric has been exported into ES
                    val results = elasticMetricsClient.rawSearch(
                        token = this.name,
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val source = result.source.asJson()
                    val ecs = source.parse<ECSEntry>()
                    assertEquals(50, ecs.ontrack?.get("value"))
                    assertEquals(this.name, ecs.labels?.get("name"))
                    assertEquals(this.project.name, ecs.labels?.get("project"))
                    assertEquals(this.branch.name, ecs.labels?.get("branch"))
                }
            }
        }
    }

    @Test
    fun `Export of run info for a validation`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    validate(vs).apply {
                        runInfoService.setRunInfo(
                            this, RunInfoInput(
                                runTime = 50,
                            )
                        )
                        // Checks the metric has been exported into ES
                        val results = elasticMetricsClient.rawSearch(
                            token = this.validationStamp.name,
                        )
                        // Expecting only one result
                        assertEquals(1, results.items.size, "One metric registered")
                        val result = results.items.first()
                        val source = result.source.asJson()
                        val ecs = source.parse<ECSEntry>()

                        assertEquals(
                            mapOf("value" to 50.0),
                            ecs.ontrack
                        )

                        assertEquals(
                            mapOf(
                                "project" to project.name,
                                "branch" to branch.name,
                                "validationStamp" to validationStamp.name,
                                "status" to "PASSED",
                            ),
                            ecs.labels
                        )

                        assertNull(ecs.tags)

                        assertEquals(
                            ECSEvent(
                                kind = ECSEventKind.metric,
                                category = "ontrack_run_validation_run_time_seconds",
                            ),
                            ecs.event
                        )
                    }
                }
            }
        }
    }

}