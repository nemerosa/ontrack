package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.PromotionRunCheckService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ProjectScmIssueInfoGraphQLIT(@Autowired private val promotionRunCheckService: PromotionRunCheckService) : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Test
    @AsAdminTest
    fun `Getting the basic commit info for a project`() {
        mockSCMTester.withMockSCMRepository {
            project {
                branch {
                    configureMockSCMBranch()

                    build("1.01") {
                        repositoryIssue("ISS-20", "Some issue")
                        val commitId = withRepositoryCommit("ISS-20 Some commit")

                        run(
                            """
                                {
                                    project(id: ${project.id}) {
                                        scmIssueInfo(issueKey: "ISS-20") {
                                            issueServiceConfigurationRepresentation {
                                                id
                                                serviceId
                                                name
                                            }
                                            issue {
                                                key
                                                displayKey
                                                summary
                                                url
                                                status {
                                                    name
                                                }
                                                rawIssue
                                            }
                                            scmCommitInfo {
                                                scmDecoratedCommit {
                                                    commit {
                                                        id
                                                        shortId
                                                        message
                                                    }
                                                    annotatedMessage
                                                }
                                            }
                                        }
                                    }
                                }
                            """
                        ) { data ->
                            val scmIssueInfo = data.path("project")
                                .path("scmIssueInfo")

                            // Issue service check
                            val issueServiceConfigurationRepresentation = scmIssueInfo.path("issueServiceConfigurationRepresentation")
                            assertEquals("mock", issueServiceConfigurationRepresentation.path("serviceId").asText())
                            assertEquals("mock//mock", issueServiceConfigurationRepresentation.path("id").asText())
                            assertEquals("mock (Mock issues)", issueServiceConfigurationRepresentation.path("name").asText())

                            // Issue check
                            val issue = scmIssueInfo.path("issue")
                            assertEquals("ISS-20", issue.path("key").asText())
                            assertEquals("ISS-20", issue.path("displayKey").asText())
                            assertEquals("Some issue", issue.path("summary").asText())

                            // Commit check
                            val scmDecoratedCommit = scmIssueInfo
                                .path("scmCommitInfo")
                                .path("scmDecoratedCommit")
                            val scmCommit = scmDecoratedCommit.path("commit")
                            assertEquals(commitId, scmCommit.path("id").asText())
                            assertEquals("ISS-20 Some commit", scmCommit.path("message").asText())
                            assertEquals(
                                """<a href="mock://Mock issues/issue/ISS-20">ISS-20</a> Some commit""",
                                scmDecoratedCommit.path("annotatedMessage").asText()
                            )
                        }
                    }
                }
            }
        }
    }

}