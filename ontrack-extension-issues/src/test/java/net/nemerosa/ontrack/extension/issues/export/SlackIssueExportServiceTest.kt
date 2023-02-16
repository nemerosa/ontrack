package net.nemerosa.ontrack.extension.issues.export

import io.mockk.mockk
import net.nemerosa.ontrack.extension.issues.mock.MockIssue
import org.junit.Test
import kotlin.test.assertEquals

class SlackIssueExportServiceTest {

    @Test
    fun format() {
        val service = SlackIssueExportService()
        val format = service.exportFormat
        assertEquals("slack", format.id)
        assertEquals("Slack", format.name)
        assertEquals("text/plain", format.type)

        val s = StringBuilder()
        service.exportAsText(
            issueServiceExtension = mockk(),
            issueServiceConfiguration = mockk(),
            groupedIssues = mapOf(
                "Features" to listOf(
                    MockIssue(1),
                    MockIssue(2),
                )
            ),
            s = s
        )

        assertEquals(
            """
                Features

                * <uri:issue/1|#1> Issue #1
                * <uri:issue/2|#2> Issue #2
            """.trimIndent(),
            s.toString().trim()
        )
    }

}
