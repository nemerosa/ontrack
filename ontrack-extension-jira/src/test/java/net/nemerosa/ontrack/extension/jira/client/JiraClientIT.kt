package net.nemerosa.ontrack.extension.jira.client

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.extension.support.client.MockRestTemplateContext
import net.nemerosa.ontrack.extension.support.client.MockRestTemplateProvider
import net.nemerosa.ontrack.extension.support.client.success
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@TestPropertySource(
    properties = [
        "ontrack.config.extension.support.client.resttemplate=mock",
    ]
)
class JiraClientIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockRestTemplateProvider: MockRestTemplateProvider
    private lateinit var mockRestTemplateContext: MockRestTemplateContext

    @Autowired
    private lateinit var jiraSessionFactory: JIRASessionFactory


    @BeforeEach
    fun init() {
        mockRestTemplateContext = mockRestTemplateProvider.createSession()
    }

    @AfterEach
    fun close() {
        mockRestTemplateContext.close()
    }

    @Test
    @AsAdminTest
    fun `Getting last commit for an issue`() {
        val configuration = JIRAConfiguration(
            name = "Jira",
            url = "http://jira",
            user = "test",
            password = "token",
        )

        mockRestTemplateContext.onGetJson(
            uri = "http://jira/rest/api/2/issue/PRJ-136",
            parameters = mapOf(
                "expand" to "names",
            ),
            outcome = success(
                TestUtils.resourceJson("/issue.json")
            )
        )

        mockRestTemplateContext.onGetJson(
            uri = "http://jira/rest/dev-status/1.0/issue/detail",
            parameters = mapOf(
                "issueId" to "304767",
                "applicationType" to "github",
                "dataType" to "repository",
            ),
            outcome = success(
                mapOf(
                    "errors" to emptyList<String>(),
                    "details" to listOf(
                        mapOf(
                            "repositories" to listOf(
                                mapOf(
                                    "name" to "yontrack/yontrack",
                                    "commits" to listOf(
                                        mapOf(
                                            "id" to "901130bc4655a8180c2034f0619768c7343095bb"
                                        )
                                    )
                                )
                            )
                        )
                    ),
                )
            )
        )

        val session = jiraSessionFactory.create(configuration)
        val client = session.client

        val commit = client.getIssueLastCommit(
            key = "PRJ-136",
            configuration = configuration,
            applicationType = "github",
            repositoryName = "yontrack/yontrack",
        )

        // Checks that the calls have been done
        mockRestTemplateContext.verify()

        assertEquals("901130bc4655a8180c2034f0619768c7343095bb", commit)
    }

}