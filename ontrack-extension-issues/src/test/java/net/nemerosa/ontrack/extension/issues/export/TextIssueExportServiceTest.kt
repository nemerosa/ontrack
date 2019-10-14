package net.nemerosa.ontrack.extension.issues.export

import org.junit.Test
import kotlin.test.assertEquals

class TextIssueExportServiceTest {

    @Test
    fun format() {
        val service = TextIssueExportService()
        val format = service.exportFormat
        assertEquals("text", format.id)
        assertEquals("Text", format.name)
        assertEquals("text/plain", format.type)
    }

}
