package net.nemerosa.ontrack.extension.github.ingestion.support

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class IngestionImageRefParserTest {

    @Test
    fun `Default protocol`() {
        val ref = IngestionImageRefParser.parseRef("just/a/path")
        assertEquals("github", ref.protocol)
        assertEquals("just/a/path", ref.path)
    }

    @Test
    fun `Specific protocol`() {
        val ref = IngestionImageRefParser.parseRef("any:just/a/path")
        assertEquals("any", ref.protocol)
        assertEquals("just/a/path", ref.path)
    }

}