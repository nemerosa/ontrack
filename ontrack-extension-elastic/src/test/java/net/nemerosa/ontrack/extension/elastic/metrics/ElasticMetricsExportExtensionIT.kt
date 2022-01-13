package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@TestPropertySource(properties = [
    "management.metrics.export.elastic.enabled=true",
])
class ElasticMetricsExportExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var metricsExportService: MetricsExportService

    @Autowired
    private lateinit var searchService: SearchService

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
        val results = searchService.rawSearch(
                token = metric,
                indexName = ElasticMetricsSearchIndexer.INDEX,
        )
        // Expecting one result
        assertEquals(1, results.items.size, "One metric registered")
    }

}