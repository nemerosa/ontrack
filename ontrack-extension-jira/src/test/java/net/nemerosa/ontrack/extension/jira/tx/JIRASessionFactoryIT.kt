package net.nemerosa.ontrack.extension.jira.tx

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.extension.jira.JIRAFixtures
import net.nemerosa.ontrack.extension.support.client.MockRestTemplateContext
import net.nemerosa.ontrack.extension.support.client.MockRestTemplateProvider
import net.nemerosa.ontrack.extension.support.client.success
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "ontrack.config.extension.support.client.resttemplate=mock",
    ]
)
class JIRASessionFactoryIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockRestTemplateProvider: MockRestTemplateProvider
    private lateinit var mockRestTemplateContext: MockRestTemplateContext

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService

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
    fun `Basic authentication`() {
        testGetIssueAuthorization(
            configuration = JIRAFixtures.jiraConfiguration(
                user = "some-user",
                password = "some-password",
            ),
            expectedAuthorizationHeader = "Basic c29tZS11c2VyOnNvbWUtcGFzc3dvcmQ="
        )
    }

    @Test
    fun `Bearer authentication`() {
        testGetIssueAuthorization(
            configuration = JIRAFixtures.jiraConfiguration(
                user = "",
                password = "some-token",
            ),
            expectedAuthorizationHeader = "Bearer some-token"
        )
    }

    private fun testGetIssueAuthorization(
        configuration: JIRAConfiguration,
        expectedAuthorizationHeader: String,
    ) {
        asAdmin {

            withDisabledConfigurationTest {
                jiraConfigurationService.newConfiguration(
                    configuration
                )
            }

            // Mocking the call to Jira
            mockRestTemplateContext.onGetJson(
                uri = "http://jira/rest/api/2/issue/ISS-123",
                parameters = mapOf(
                    "expand" to "names",
                ),
                outcome = success(
                    mapOf(
                        "fields" to mapOf(
                            "project" to mapOf(
                                "key" to "ISS",
                            ),
                            "summary" to "Test issue",
                            "issuetype" to mapOf(
                                "name" to "Test"
                            ),
                            // "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                            "updated" to "2024-04-16T14:34:00.000+0000",
                            "labels" to listOf("test"),
                            "description" to "Test issue",
                            "assignee" to mapOf(
                                "name" to "some-user"
                            ),
                            "fixVersions" to listOf(
                                mapOf(
                                    "name" to "v1"
                                )
                            ),
                        )
                    )
                ),
                expectedHeaders = mapOf(
                    "Authorization" to expectedAuthorizationHeader
                ),
            )

            val session = jiraSessionFactory.create(configuration)
            val client = session.client
            client.getIssue("ISS-123", configuration)

            mockRestTemplateContext.verify()
        }
    }

}