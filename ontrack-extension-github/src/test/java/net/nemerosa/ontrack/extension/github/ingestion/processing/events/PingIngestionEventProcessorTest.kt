package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import org.junit.jupiter.api.Test
import kotlin.test.assertNull

internal class PingIngestionEventProcessorTest {

    @Test
    fun `No source for the ping processing`() {
        val processor = PingIngestionEventProcessor()
        assertNull(processor.getPayloadSource(PingPayload("test")), "No source for the ping event")
    }

}