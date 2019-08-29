package net.nemerosa.ontrack.extension.issues.export

import org.junit.Test
import kotlin.test.assertEquals

class TextIssueExportServiceTest {

    private val service = TextIssueExportService()

    @Test
    fun format() {
        val format = service.exportFormat
        assertEquals("text", format.id)
        assertEquals("Text", format.name)
        assertEquals("text/plain", format.type)
    }

    @Test
    fun `Title section without title`() {
        assertEquals("""
            Some content
        """.trimIndent(), service.exportSectionAsText(
                null,
                SectionType.TITLE,
                "Some content"
        ))
    }

    @Test
    fun `Title section with title`() {
        assertEquals("""
            The title
            =========
            
            Some content
        """.trimIndent(), service.exportSectionAsText(
                "The title",
                SectionType.TITLE,
                "Some content"
        ))
    }

    @Test
    fun `Heading section without title`() {
        assertEquals("""
            Some content
        """.trimIndent(), service.exportSectionAsText(
                null,
                SectionType.HEADING,
                "Some content"
        ))
    }

    @Test
    fun `Heading section with title`() {
        assertEquals("""
            ## The heading
            
            Some content
        """.trimIndent(), service.exportSectionAsText(
                "The heading",
                SectionType.HEADING,
                "Some content"
        ))
    }

}
