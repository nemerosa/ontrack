package net.nemerosa.ontrack.extension.github.ingestion

import net.nemerosa.ontrack.extension.github.ingestion.queue.AsyncIngestionHookQueue
import net.nemerosa.ontrack.extension.github.ingestion.queue.IngestionHookQueue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Integration test using RabbitMQ.
 */
@TestPropertySource(
    properties = [
        // Overriding the settings in AbstractIngestionTestSupport
        "ontrack.extension.github.ingestion.queue.async=true",
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.username=ontrack",
        "spring.rabbitmq.password=ontrack",
    ]
)
class IngestionHookRabbitMQIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionConfigProperties: IngestionConfigProperties

    @Autowired
    private lateinit var ingestionHookQueue: IngestionHookQueue

    @Test
    fun `Configuration check`() {
        assertTrue(ingestionConfigProperties.queue.async, "Running in async mode")
        assertIs<AsyncIngestionHookQueue>(ingestionHookQueue, "Running in async mode")
    }

}