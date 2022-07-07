package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@TestPropertySource(properties = [
    "ontrack.extension.elastic.metrics.enabled=true",
    "ontrack.extension.elastic.metrics.index.immediate=true",
])
class ElasticMetricsExportExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var metricsExportService: MetricsExportService

    @Autowired
    private lateinit var elasticMetricsClient: ElasticMetricsClient

    @Test
    fun `Exporting metrics to Elastic`() {
        val metric = uid("m")
        val tags = mapOf(
            "environment" to "development",
            "region" to "BE",
        )
        val values = mapOf(
            "old" to 1.0,
            "new" to 2.0,
        )
        val timestamp = Time.now()
        metricsExportService.exportMetrics(metric, tags, values, timestamp)
        // Checks the metric has been exported into ES
        val results = elasticMetricsClient.rawSearch(
            token = metric,
        )
        // Expecting only one result
        assertEquals(1, results.items.size, "One metric registered")
        val result = results.items.first()
        val source = result.source.asJson().parse<ECSEntry>()

        assertEquals(metric, source.event.category)
        assertEquals(tags, source.labels)
        assertEquals(values, source.ontrack)
    }

    @Test
    fun `Unique documents for the metrics in ElasticSearch`() {
        val metric = uid("m")
        val project = uid("prj-")
        val branch = uid("b-")
        val tags = mapOf(
            "project" to project,
            "branch" to branch,
        )
        val values = mapOf(
            "old" to 1.0,
            "new" to 2.0,
        )
        val timestamp = Time.now()
        metricsExportService.exportMetrics(metric, tags, values, timestamp)

        // Checks the metric has been exported into ES
        val results = elasticMetricsClient.rawSearch(
            token = "$project $branch",
        ).items.filter { it.score > 1 }
        assertEquals(1, results.size, "One metric registered")
        val result = results.first()
        val initialId = result.id
        val source = result.source.asJson().parse<ECSEntry>()
        assertEquals(metric, source.event.category)
        assertEquals(tags, source.labels)
        assertEquals(values, source.ontrack)

        // Re-exporting the metric
        metricsExportService.exportMetrics(metric, tags, values, timestamp)

        // Checking the value again
        val newResults = elasticMetricsClient.rawSearch(
            token = "$project $branch",
        ).items.filter { it.score > 1 }
        assertEquals(1, newResults.size, "One metric registered")
        val newResult = newResults.first()
        assertEquals(initialId, newResult.id, "Same document is returned")
        val newSource = newResult.source.asJson().parse<ECSEntry>()
        assertEquals(metric, newSource.event.category)
        assertEquals(tags, newSource.labels)
        assertEquals(values, newSource.ontrack)
    }

}