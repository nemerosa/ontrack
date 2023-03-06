package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitChangeLogBranchGraphQLFieldContributorIT : AbstractGitTestSupport() {

    @Test
    fun `Getting a change log from the branch`() {
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

                    // Getting the change log between build 5 and 7
                    run(
                        """
                            {
                                branches(id: $id) {
                                    gitChangeLog(from: "5", to: "7") {
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
                        val messages = data["branches"][0]["gitChangeLog"]["commits"].map {
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