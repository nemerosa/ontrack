package net.nemerosa.ontrack.extension.github.ingestion

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.search.MeterNotFoundException
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookRabbitMQRepositoryIT.Companion.TEST_REPOSITORY
import net.nemerosa.ontrack.extension.github.ingestion.metrics.INGESTION_METRIC_QUEUE_TAG
import net.nemerosa.ontrack.extension.github.ingestion.metrics.INGESTION_METRIC_ROUTING_TAG
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration test using RabbitMQ and a specific repository queue.
 */
@TestPropertySource(
    properties = [
        // Overriding the settings in AbstractIngestionTestSupport
        "ontrack.extension.github.ingestion.processing.repositories.test.repository=$TEST_REPOSITORY",
    ]
)
@Ignore
class IngestionHookRabbitMQRepositoryIT : AbstractIngestionHookRabbitMQTestSupport() {

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    /**
     * Using a fixed repository name for the test (allows for the configuration of the settings)
     */
    override val repositoryName: String = TEST_REPOSITORY

    /**
     * Counters
     */
    private var routingCounter = 0.0
    private var targetCounter = 0.0
    private var defaultCounter = 0.0

    /**
     * The project associated to the test repository must be destroyed first and the payloads must be purged.
     */
    @Before
    fun cleanup() {
        asAdmin {
            ingestionHookPayloadStorage.cleanUntil(Time.now())
            structureService.findProjectByName(TEST_REPOSITORY).getOrNull()?.let {
                structureService.deleteProject(it.id)
            }
        }
    }

    @Test
    fun `Checking the configuration`() {
        assertEquals(1, ingestionConfigProperties.processing.repositories.size)
        assertNotNull(ingestionConfigProperties.processing.repositories["test"]) { repository ->
            assertEquals(null, repository.owner)
            assertEquals(TEST_REPOSITORY, repository.repository)
            assertEquals(10U, repository.config.concurrency)
        }
    }

    override fun preChecks() {
        // Getting the counters we want to measure
        val routing =
            meterRegistry.findCounter(
                IngestionMetrics.Queue.producedCount,
                INGESTION_METRIC_ROUTING_TAG,
                "test"
            )
        val target = meterRegistry.findCounter(
            IngestionMetrics.Queue.consumedCount,
            INGESTION_METRIC_QUEUE_TAG,
            "github.ingestion.test"
        )
        val default = meterRegistry.findCounter(
            IngestionMetrics.Queue.consumedCount,
            INGESTION_METRIC_QUEUE_TAG,
            "github.ingestion.default"
        )
        // Getting initial counts
        routingCounter = routing?.count() ?: 0.0
        targetCounter = target?.count() ?: 0.0
        defaultCounter = default?.count() ?: 0.0
    }

    override fun postChecks() {
        // Getting the counters we want to measure
        val routing =
            meterRegistry.findCounter(
                IngestionMetrics.Queue.producedCount,
                INGESTION_METRIC_ROUTING_TAG,
                "test"
            )
        val target = meterRegistry.findCounter(
            IngestionMetrics.Queue.consumedCount,
            INGESTION_METRIC_QUEUE_TAG,
            "github.ingestion.test"
        )
        val default = meterRegistry.findCounter(
            IngestionMetrics.Queue.consumedCount,
            INGESTION_METRIC_QUEUE_TAG,
            "github.ingestion.default"
        )
        // Getting new counts
        val newRoutingCounter = routing?.count() ?: 0.0
        val newTargetCounter = target?.count() ?: 0.0
        val newDefaultCounter = default?.count() ?: 0.0
        // Checking the differences
        assertTrue(newRoutingCounter > routingCounter, "Test routing has been used")
        assertTrue(newTargetCounter > targetCounter, "Test queue has been used")
        assertEquals(defaultCounter, newDefaultCounter, "Default queue has not been used")
    }

    private fun MeterRegistry.findCounter(name: String, tagKey: String, tagValue: String): Counter? =
        try {
            get(name).tag(tagKey, tagValue).counters().firstOrNull()
        } catch (_: MeterNotFoundException) {
            null
        }

    companion object {
        const val TEST_REPOSITORY = "ontrack-extension-github-ingestion"
    }

}
