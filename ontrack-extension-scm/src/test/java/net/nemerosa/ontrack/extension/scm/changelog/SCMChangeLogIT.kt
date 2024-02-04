package net.nemerosa.ontrack.extension.scm.changelog

import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SCMChangeLogIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var scmChangeLogService: SCMChangeLogService

    @Test
    fun `Getting a change log using the SCM API`() {
        asAdmin {
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()

                        build {}
                        val from = build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                        }
                        build {
                            repositoryIssue("ISS-21", "Some new feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        build {
                            repositoryIssue("ISS-22", "Some fixes are needed")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build {
                            repositoryIssue("ISS-23", "Some nicer UI")
                            withRepositoryCommit("ISS-23 Fixing some CSS")

                            val changeLog = runBlocking {
                                scmChangeLogService.getChangeLog(
                                    from = from,
                                    to = this@build,
                                )
                            }

                            // Checking the commits
                            assertEquals(
                                listOf(
                                    "ISS-23 Fixing some CSS",
                                    "ISS-22 Fixing some bugs",
                                    "ISS-21 Some fixes for a feature",
                                    "ISS-21 Some commits for a feature"
                                ),
                                changeLog.commits.map { it.message },
                                "Change log commits"
                            )

                            // Checking the issues
                            assertEquals(
                                listOf(
                                    "ISS-21" to "Some new feature",
                                    "ISS-22" to "Some fixes are needed",
                                    "ISS-23" to "Some nicer UI",
                                ),
                                changeLog.issues?.issues?.map { it.displayKey to it.summary },
                                "Change log issues"
                            )

                        }
                    }
                }
            }
        }
    }

}