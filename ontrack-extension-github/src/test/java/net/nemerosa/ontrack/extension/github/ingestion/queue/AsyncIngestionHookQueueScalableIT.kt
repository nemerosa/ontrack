package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.it.waitUntil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@TestPropertySource(
    properties = [
        // Sending messages outside of the transaction
        "ontrack.config.rabbitmq.transactional=false",
        // Using async messaging
        "ontrack.extension.github.ingestion.processing.async=true",
        // Number of queues to generate
        "ontrack.extension.github.ingestion.processing.scale=10",
    ]
)
@ContextConfiguration(classes = [AsyncIngestionHookQueueScalableConfiguration::class])
class AsyncIngestionHookQueueScalableIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var storage: IngestionHookPayloadStorage

    @Autowired
    private lateinit var queue: IngestionHookQueue

    @ExperimentalTime
    @Test
    fun `Processing a payload on a separate queue`() {
        val payload = IngestionHookFixtures.sampleWorkflowRunIngestionPayload()
        val expectedQueue = abs(payload.repository!!.fullName.hashCode()) % 10
        asAdmin {
            storage.store(payload, "source")
            queue.queue(payload)
            waitUntil(message = "Queue has been assigned", timeout = 20.seconds) {
                val queueName = storage.findByUUID(payload.uuid.toString())?.queue
                queueName == "github.ingestion.default.$expectedQueue"
            }
        }
    }

}