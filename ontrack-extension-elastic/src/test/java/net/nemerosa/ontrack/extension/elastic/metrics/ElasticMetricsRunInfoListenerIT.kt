package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
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
                        indexName = "ontrack_metric_run_info_build",
                    )
                    // Expecting only one result
                    assertEquals(1, results.items.size, "One metric registered")
                    val result = results.items.first()
                    val source = result.source.asJson()
                    assertEquals(50.0, source["fields"]["value"].asDouble())
                    assertEquals(this.name, source["fields"]["name"].asText())
                    assertEquals(this.project.name, source["tags"]["project"].asText())
                    assertEquals(this.branch.name, source["tags"]["branch"].asText())
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
                            indexName = "ontrack_metric_run_info_validation_run",
                        )
                        // Expecting only one result
                        assertEquals(1, results.items.size, "One metric registered")
                        val result = results.items.first()
                        val source = result.source.asJson()
                        assertEquals(50.0, source["fields"]["value"].asDouble())
                        assertEquals(this.build.name, source["fields"]["name"].asText())
                        assertEquals(this.project.name, source["tags"]["project"].asText())
                        assertEquals(this.validationStamp.branch.name, source["tags"]["branch"].asText())
                        assertEquals(this.validationStamp.name, source["tags"]["validationStamp"].asText())
                        assertEquals("PASSED", source["tags"]["status"].asText())
                    }
                }
            }
        }
    }

}