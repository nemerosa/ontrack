package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunInfoService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@TestPropertySource(properties = [
    "ontrack.extension.elastic.metrics.enabled=true",
    "ontrack.extension.elastic.metrics.index.immediate=true",
])
class ElasticMetricsRunInfoListenerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var elasticMetricsClient: ElasticMetricsClient

    @Autowired
    private lateinit var runInfoService: RunInfoService

    @Test
    fun `Export of run info for a build`() {
        project {
            branch {
                build {
                    runInfoService.setRunInfo(this, RunInfoInput(
                        runTime = 50,
                    ))
                    // Checks the metric has been exported into ES
                    val results = elasticMetricsClient.rawSearch(
                        token = this.name,
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val source = result.source.asJson()
                    val ecs = source.parse<ECSEntry>()
                    assertEquals(50_000_000_000L, ecs.event.duration)
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
                        runInfoService.setRunInfo(this, RunInfoInput(
                            runTime = 50,
                        ))
                        // Checks the metric has been exported into ES
                        val results = elasticMetricsClient.rawSearch(
                            token = this.validationStamp.name,
                        )
                        // Expecting only one result
                        assertEquals(1, results.items.size, "One metric registered")
                        val result = results.items.first()
                        val source = result.source.asJson()
                        val ecs = source.parse<ECSEntry>()
                        assertEquals(50_000_000_000L, ecs.event.duration)
                        assertEquals(this.build.name, ecs.labels?.get("name"))
                        assertEquals(this.project.name, ecs.labels?.get("project"))
                        assertEquals(this.validationStamp.branch.name, ecs.labels?.get("branch"))
                        assertEquals(this.validationStamp.name, ecs.labels?.get("validationStamp"))
                        assertEquals("PASSED", ecs.labels?.get("status"))
                    }
                }
            }
        }
    }

}