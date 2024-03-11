package net.nemerosa.ontrack.extension.issues.combined

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.export.ExportFormat
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues
import net.nemerosa.ontrack.extension.issues.mock.TestIssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.model.support.MessageAnnotation
import net.nemerosa.ontrack.model.support.MessageAnnotator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class CombinedIssueServiceExtensionTest {

    private lateinit var service: CombinedIssueServiceExtension
    private lateinit var configuration: CombinedIssueServiceConfiguration
    private lateinit var type1IssueService: IssueServiceExtension
    private lateinit var type2IssueService: IssueServiceExtension
    private lateinit var testConfiguration: TestIssueServiceConfiguration

    @BeforeEach
    fun setup() {
        val issueServiceRegistry = mockk<IssueServiceRegistry>()
        val configurationService = mockk<CombinedIssueServiceConfigurationService>()
        service = CombinedIssueServiceExtension(
            CombinedIssueServiceExtensionFeature(),
            issueServiceRegistry,
            configurationService
        )

        configuration = CombinedIssueServiceConfiguration(
            "test",
            listOf("type1:test", "type2:test")
        )

        type1IssueService = mockk<IssueServiceExtension>()

        type2IssueService = mockk<IssueServiceExtension>()

        testConfiguration = TestIssueServiceConfiguration("test")

        every {
            issueServiceRegistry.getConfiguredIssueService("type1:test")
        } returns
                ConfiguredIssueService(
                    type1IssueService,
                    testConfiguration
                )

        every {
            issueServiceRegistry.getConfiguredIssueService("type2:test")
        } returns
                ConfiguredIssueService(
                    type2IssueService,
                    testConfiguration
                )
    }

    @Test
    fun `Extraction of issue keys`() {
        val message = "ONTRACK-1 #1 A message"

        every {
            type1IssueService.extractIssueKeysFromMessage(testConfiguration, message)
        } returns
                setOf("ONTRACK-1")

        every {
            type2IssueService.extractIssueKeysFromMessage(testConfiguration, message)
        } returns
                setOf("1")

        val keys = service.extractIssueKeysFromMessage(configuration, message)
        assertEquals(
            setOf("ONTRACK-1", "1"),
            keys
        )
    }

    @Test
    fun `No issue found`() {
        every { type1IssueService.getIssue(testConfiguration, "1") } returns null
        every { type2IssueService.getIssue(testConfiguration, "1") } returns null
        assertNull(service.getIssue(configuration, "1"))
    }

    @Test
    fun `One issue found - 1`() {
        val issue1 = mockk<Issue>()
        every {
            type1IssueService.getIssue(testConfiguration, "1")
        } returns issue1
        every { type2IssueService.getIssue(testConfiguration, "1") } returns null
        assertEquals(issue1, service.getIssue(configuration, "1"))
    }

    @Test
    fun `One issue found - 2`() {
        every { type1IssueService.getIssue(testConfiguration, "1") } returns null
        val issue2 = mockk<Issue>()
        every {
            type2IssueService.getIssue(testConfiguration, "1")
        } returns issue2
        assertEquals(issue2, service.getIssue(configuration, "1"))
    }

    @Test
    fun `Two issues found - takes the first one`() {
        val issue1 = mockk<Issue>()
        val issue2 = mockk<Issue>()
        every {
            type1IssueService.getIssue(testConfiguration, "1")
        } returns issue1
        every {
            type2IssueService.getIssue(testConfiguration, "1")
        } returns issue2
        assertEquals(issue1, service.getIssue(configuration, "1"))
    }

    @Test
    fun `Getting the issue ID - not valid for both`() {
        every {
            type1IssueService.getIssueId(testConfiguration, "X")
        } returns null
        every {
            type2IssueService.getIssueId(testConfiguration, "X")
        } returns null
        assertNull(service.getIssueId(configuration, "X"))
    }

    @Test
    fun `Getting the issue ID - valid for first`() {
        every {
            type1IssueService.getIssueId(testConfiguration, "#1")
        } returns "1"
        every {
            type2IssueService.getIssueId(testConfiguration, "#1")
        } returns null
        assertEquals("1", service.getIssueId(configuration, "#1"))
    }

    @Test
    fun `Getting the issue ID - valid for second`() {
        every {
            type1IssueService.getIssueId(testConfiguration, "#1")
        } returns null
        every {
            type2IssueService.getIssueId(testConfiguration, "#1")
        } returns "1"
        assertEquals("1", service.getIssueId(configuration, "#1"))
    }

    @Test
    fun `Getting the issue ID - valid for both - takes the first`() {
        every {
            type1IssueService.getIssueId(testConfiguration, "#1")
        } returns "11"
        every {
            type2IssueService.getIssueId(testConfiguration, "#1")
        } returns "12"
        assertEquals("11", service.getIssueId(configuration, "#1"))
    }

    @Test
    fun `Message annotator - none returned`() {
        every {
            type1IssueService.getMessageAnnotator(testConfiguration)
        } returns null
        every {
            type2IssueService.getMessageAnnotator(testConfiguration)
        } returns null
        assertNull(service.getMessageAnnotator(configuration))
    }

    @Test
    fun `Message annotator - first returned`() {
        val text = "#1 Issue"

        val messageAnnotator1 = mockk<MessageAnnotator>()
        val annotation1 = MessageAnnotation("type1", "text1")
        every {
            messageAnnotator1.annotate(text)
        } returns listOf(annotation1)

        every {
            type1IssueService.getMessageAnnotator(testConfiguration)
        } returns messageAnnotator1
        every {
            type2IssueService.getMessageAnnotator(testConfiguration)
        } returns null

        val messageAnnotator = service.getMessageAnnotator(configuration)
        assertNotNull(messageAnnotator) {
            assertEquals(
                setOf(annotation1),
                it.annotate(text)
            )
        }
    }

    @Test
    fun `Message annotator - second returned`() {
        val text = "#1 Issue"

        val messageAnnotator2 = mockk<MessageAnnotator>()
        val annotation21 = MessageAnnotation("type2", "text21")
        val annotation22 = MessageAnnotation("type2", "text22")
        every {
            messageAnnotator2.annotate(text)
        } returns listOf(annotation21, annotation22)

        every {
            type1IssueService.getMessageAnnotator(testConfiguration)
        } returns null
        every {
            type2IssueService.getMessageAnnotator(testConfiguration)
        } returns messageAnnotator2

        val messageAnnotator = service.getMessageAnnotator(configuration)
        assertNotNull(messageAnnotator) {
            assertEquals(
                setOf(annotation21, annotation22),
                it.annotate(text)
            )
        }
    }

    @Test
    fun `Message annotator - all returned`() {
        val text = "#1 Issue"

        val messageAnnotator1 = mockk<MessageAnnotator>()
        val annotation1 = MessageAnnotation("type1", "text1")
        every {
            messageAnnotator1.annotate(text)
        } returns listOf(annotation1)

        val messageAnnotator2 = mockk<MessageAnnotator>()
        val annotation21 = MessageAnnotation("type2", "text21")
        val annotation22 = MessageAnnotation("type2", "text22")
        every {
            messageAnnotator2.annotate(text)
        } returns listOf(annotation21, annotation22)

        every {
            type1IssueService.getMessageAnnotator(testConfiguration)
        } returns messageAnnotator1
        every {
            type2IssueService.getMessageAnnotator(testConfiguration)
        } returns messageAnnotator2

        val messageAnnotator = service.getMessageAnnotator(configuration)
        assertNotNull(messageAnnotator) {
            assertEquals(
                setOf(annotation1, annotation21, annotation22),
                it.annotate(text)
            )
        }
    }

    @Test
    fun `Export formats`() {

        every {
            type1IssueService.exportFormats(testConfiguration)
        } returns listOf(ExportFormat.HTML, ExportFormat.TEXT)
        every {
            type2IssueService.exportFormats(testConfiguration)
        } returns listOf(ExportFormat.MARKDOWN, ExportFormat.TEXT)

        val formats = service.exportFormats(configuration).toSet()

        assertEquals(
            setOf(ExportFormat.HTML, ExportFormat.MARKDOWN, ExportFormat.TEXT),
            formats
        )
    }

    @Test
    fun `Export issues with no issue`() {

        val issues = emptyList<Issue>()

        val exportRequest = IssueChangeLogExportRequest()
        exportRequest.format = "text"

        every {
            type1IssueService.exportIssues(testConfiguration, issues, exportRequest)
        } returns ExportedIssues("text", "")

        every {
            type2IssueService.exportIssues(testConfiguration, issues, exportRequest)
        } returns ExportedIssues("text", "")

        val export = service.exportIssues(configuration, issues, exportRequest)

        assertEquals("text", export.format)
        assertEquals("", export.content)
    }

    @Test
    fun `Export issues`() {

        val issue1 = mockk<Issue>()
        val issue2 = mockk<Issue>()

        val issues = listOf(issue1, issue2)

        val exportRequest = IssueChangeLogExportRequest()
        exportRequest.format = "text"

        every {
            type1IssueService.exportIssues(testConfiguration, issues, exportRequest)
        } returns ExportedIssues(
            "text", """
            #1 Issue 1
            """.trimIndent()
        )

        every {
            type2IssueService.exportIssues(testConfiguration, issues, exportRequest)
        } returns ExportedIssues(
            "text", """
            PRJ-2 Issue 2
            """.trimIndent()
        )

        val export = service.exportIssues(configuration, issues, exportRequest)

        assertEquals("text", export.format)
        assertEquals(
            """
                #1 Issue 1
                PRJ-2 Issue 2
                """.trimIndent(),
            export.content
        )


    }

}
