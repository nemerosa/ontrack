package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ProjectScmCommitInfoGraphQLIT : AbstractQLKTITSupport() {

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
                                        scmCommitInfo(commitId: "$commitId") {
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
                            """
                        ) { data ->
                            val scmDecoratedCommit = data.path("project")
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