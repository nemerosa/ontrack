package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLRootQuerySCMChangeLogIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Test
    fun `Getting a change log using the SCM API`() {
        asAdmin {
            val dependency = project {
                branch {
                    build("3.0.1")
                    build("3.0.4")
                }
            }
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()

                        build("1.01") {}
                        val from = build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                            // Link to change
                            linkTo(dependency, "3.0.1")
                        }
                        build("1.02") {
                            repositoryIssue("ISS-21", "Some new feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        build("1.03") {
                            repositoryIssue("ISS-22", "Some fixes are needed")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build("1.04") {
                            repositoryIssue("ISS-23", "Some nicer UI")
                            withRepositoryCommit("ISS-23 Fixing some CSS")
                            // Link to change
                            linkTo(dependency, "3.0.4")

                            run(
                                """
                                {
                                    branch(id: ${from.branch.id}) {
                                        builds {
                                            name
                                        }
                                    }
                                    scmChangeLog(
                                        from: ${from.id},
                                        to: ${this@build.id}
                                    ) {
                                        linkChanges {
                                            project {
                                                name
                                            }
                                            from {
                                                name
                                            }
                                            to {
                                                name
                                            }
                                        }
                                        commits {
                                            commit {
                                                message
                                            }
                                            annotatedMessage
                                            build {
                                                name
                                            }
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
                                data.path("branch").path("builds").forEach { build ->
                                    println(build.path("name").asText())
                                }
                                val changeLog = data.path("scmChangeLog")
                                assertEquals(
                                    mapOf(
                                        "linkChanges" to listOf(
                                            mapOf(
                                                "project" to mapOf(
                                                    "name" to dependency.name
                                                ),
                                                "from" to mapOf(
                                                    "name" to "3.0.1"
                                                ),
                                                "to" to mapOf(
                                                    "name" to "3.0.4"
                                                ),
                                            )
                                        ),
                                        "commits" to listOf(
                                            mapOf(
                                                "commit" to mapOf(
                                                    "message" to "ISS-23 Fixing some CSS",
                                                ),
                                                "annotatedMessage" to """<a href="mock://Mock issues/issue/ISS-23">ISS-23</a> Fixing some CSS""",
                                                "build" to mapOf(
                                                    "name" to "1.04"
                                                ),
                                            ),
                                            mapOf(
                                                "commit" to mapOf(
                                                    "message" to "ISS-22 Fixing some bugs"
                                                ),
                                                "annotatedMessage" to """<a href="mock://Mock issues/issue/ISS-22">ISS-22</a> Fixing some bugs""",
                                                "build" to mapOf(
                                                    "name" to "1.03"
                                                ),
                                            ),
                                            mapOf(
                                                "commit" to mapOf(
                                                    "message" to "ISS-21 Some fixes for a feature"
                                                ),
                                                "annotatedMessage" to """<a href="mock://Mock issues/issue/ISS-21">ISS-21</a> Some fixes for a feature""",
                                                "build" to mapOf(
                                                    "name" to "1.02"
                                                ),
                                            ),
                                            mapOf(
                                                "commit" to mapOf(
                                                    "message" to "ISS-21 Some commits for a feature"
                                                ),
                                                "annotatedMessage" to """<a href="mock://Mock issues/issue/ISS-21">ISS-21</a> Some commits for a feature""",
                                                "build" to null,
                                            ),
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

    @Test
    fun `Rendering a change log using the SCM API`() {
        asAdmin {
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()

                        build("1.01") {}
                        val from = build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                        }
                        build("1.02") {
                            repositoryIssue("ISS-21", "Some new feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        build("1.03") {
                            repositoryIssue("ISS-22", "Some fixes are needed")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build("1.04") {
                            repositoryIssue("ISS-23", "Some nicer UI")
                            withRepositoryCommit("ISS-23 Fixing some CSS")

                            run(
                                """
                                {
                                    branch(id: ${from.branch.id}) {
                                        builds {
                                            name
                                        }
                                    }
                                    scmChangeLog(
                                        from: ${from.id},
                                        to: ${this@build.id}
                                    ) {
                                        render(
                                            renderer: "markdown",
                                            config: {
                                                title: true,
                                                commitsOption: ALWAYS,
                                            },
                                        )
                                    }
                                }
                            """.trimIndent()
                            ) { data ->
                                val changeLog = data.path("scmChangeLog")
                                    .path("render").asText()
                                assertEquals(
                                    """
                                        ## Change log for [${project.name}](http://localhost:3000/project/${project.id}) from [${from.name}](http://localhost:3000/build/${from.id}) to [${this@build.name}](http://localhost:3000/build/${this@build.id})

                                        * [ISS-21](mock://${repositoryName}/issue/ISS-21) Some new feature
                                        * [ISS-22](mock://${repositoryName}/issue/ISS-22) Some fixes are needed
                                        * [ISS-23](mock://${repositoryName}/issue/ISS-23) Some nicer UI

                                        Commits:

                                        * [main-5-543d857](mock://${repositoryName}/main-5-543d857) ISS-23 Fixing some CSS
                                        * [main-4-dacf415](mock://${repositoryName}/main-4-dacf415) ISS-22 Fixing some bugs
                                        * [main-3-a847748](mock://${repositoryName}/main-3-a847748) ISS-21 Some fixes for a feature
                                        * [main-2-dfbccd9](mock://${repositoryName}/main-2-dfbccd9) ISS-21 Some commits for a feature
                                    """.trimIndent(),
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