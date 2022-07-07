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
import java.time.LocalDateTime
import kotlin.test.assertEquals

/**
 * Testing the re-export of metrics for ElasticSearch.
 */
@TestPropertySource(
    properties = [
        "ontrack.extension.elastic.metrics.enabled=true",
        "ontrack.extension.elastic.metrics.index.immediate=true",
    ]
)
class ElasticMetricsReexportExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var metricsExportService: MetricsExportService

    @Autowired
    private lateinit var elasticMetricsClient: ElasticMetricsClient

    @Autowired
    private lateinit var elasticMetricsExportExtension: ElasticMetricsExportExtension

    @Test
    fun `Exporting metrics to Elastic`() {
        // Data to export
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

        // Export and check
        exportAndCheck(metric, tags, values, timestamp)

        // Re-export preparation
        elasticMetricsExportExtension.prepareReexport()

        // Export and check
        exportAndCheck(metric, tags, values, timestamp)

    }

    private fun exportAndCheck(
        metric: String,
        tags: Map<String, String>,
        values: Map<String, Double>,
        timestamp: LocalDateTime,
    ) {
        metricsExportService.exportMetrics(metric, tags, values, timestamp)
        val results = elasticMetricsClient.rawSearch(
            token = "development",
        )
        assertEquals(1, results.items.size, "One metric registered")
        val result = results.items.first()
        val source = result.source.asJson().parse<ECSEntry>()
        assertEquals(metric, source.event.category)
        assertEquals(tags, source.labels)
        assertEquals(values, source.ontrack)
    }

}