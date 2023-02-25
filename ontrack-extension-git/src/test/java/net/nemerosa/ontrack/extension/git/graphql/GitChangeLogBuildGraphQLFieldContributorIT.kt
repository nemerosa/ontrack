package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitChangeLogBuildGraphQLFieldContributorIT : AbstractGitTestSupport() {

    @Test
    fun `Getting a change log from the build to another one`() {
        createRepo {
            commits(10, pauses = true)
        } and { repo, commits ->
            project {
                gitProject(repo, sync = true)
                branch {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    // Creates builds for some commits
                    listOf(2, 5, 7, 8).forEach { no ->
                        build(no.toString()) {
                            gitCommitProperty(commits.getValue(no))
                        }
                    }
                    // Getting the change log between build 5 and 8
                    run(
                        """
                            {
                                builds(project: "${project.name}", branch: "$name", name: "8") {
                                    gitChangeLog(to: "5") {
                                        commits {
                                            commit {
                                                shortMessage
                                            }
                                        }
                                    }
                                }
                            }
                        """
                    ) { data ->
                        val messages = data["builds"][0]["gitChangeLog"]["commits"].map {
                            it["commit"]["shortMessage"].asText()
                        }
                        assertEquals(
                            listOf("Commit 8", "Commit 7", "Commit 6"),
                            messages
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting a change log from the build to previous one`() {
        createRepo {
            commits(10, pauses = true)
        } and { repo, commits ->
            project {
                gitProject(repo, sync = true)
                branch {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    // Creates builds for some commits
                    listOf(2, 5, 7, 8).forEach { no ->
                        build(no.toString()) {
                            gitCommitProperty(commits.getValue(no))
                        }
                    }
                    // Getting the change log between build 5 and 8
                    run(
                        """
                            {
                                builds(project: "${project.name}", branch: "$name", name: "7") {
                                    gitChangeLog {
                                        commits {
                                            commit {
                                                shortMessage
                                            }
                                        }
                                    }
                                }
                            }
                        """
                    ) { data ->
                        val messages = data["builds"][0]["gitChangeLog"]["commits"].map {
                            it["commit"]["shortMessage"].asText()
                        }
                        assertEquals(
                            listOf("Commit 7", "Commit 6"),
                            messages
                        )
                    }
                }
            }
        }
    }

}