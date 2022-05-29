package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult.IGNORED
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult.PROCESSED
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class IngestionEventProcessingResultTest {

    @Test
    fun combinations() {
        assertEquals(IGNORED, IGNORED + IGNORED)
        assertEquals(PROCESSED, IGNORED + PROCESSED)
        assertEquals(PROCESSED, PROCESSED + IGNORED)
        assertEquals(PROCESSED, PROCESSED + PROCESSED)
    }

}