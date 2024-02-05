package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogService
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLRootQuerySCMChangeLogIT : AbstractQLKTITSupport() {

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

                            run(
                                """
                                {
                                    scmChangeLog(
                                        from: ${from.id},
                                        to: ${this@build.id}
                                    ) {
                                        commits {
                                            message
                                        }
                                        issues {
                                            issues {
                                                displayKey
                                                summary
                                                url
                                            }
                                        }
                                    }
                                }
                            """.trimIndent()
                            ) { data ->
                                val changeLog = data.path("scmChangeLog")
                                assertEquals(
                                    mapOf(
                                        "commits" to listOf(
                                            mapOf("message" to "ISS-23 Fixing some CSS"),
                                            mapOf("message" to "ISS-22 Fixing some bugs"),
                                            mapOf("message" to "ISS-21 Some fixes for a feature"),
                                            mapOf("message" to "ISS-21 Some commits for a feature"),
                                        ),
                                        "issues" to mapOf(
                                            "issues" to listOf(
                                                mapOf(
                                                    "displayKey" to "ISS-21",
                                                    "summary" to "Some new feature",
                                                    "url" to "mock://$repositoryName/issue/ISS-21",
                                                ),
                                                mapOf(
                                                    "displayKey" to "ISS-22",
                                                    "summary" to "Some fixes are needed",
                                                    "url" to "mock://$repositoryName/issue/ISS-22",
                                                ),
                                                mapOf(
                                                    "displayKey" to "ISS-23",
                                                    "summary" to "Some nicer UI",
                                                    "url" to "mock://$repositoryName/issue/ISS-23",
                                                ),
                                            )
                                        )
                                    ).asJson(),
                                    changeLog
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}