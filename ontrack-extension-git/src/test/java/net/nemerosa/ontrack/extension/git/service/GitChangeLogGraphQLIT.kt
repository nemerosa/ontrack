package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.structure.Branch
import org.junit.Test
import kotlin.test.assertEquals


/**
 * Integration tests for Git support.
 */
class GitChangeLogGraphQLIT : AbstractGitTestSupport() {

    private fun doTest(testCode: (Branch) -> Unit) {
        createRepo {
            commits(10, pauses = true)
        } and { repo, commits ->
            project {
                gitProject(repo, sync = true)
                branch {
                    gitBranch("master") {
                        commitAsProperty()
                    }

                    // Creates builds for some commits
                    listOf(2, 5, 7, 8).forEach { no ->
                        build(no.toString()) {
                            gitCommitProperty(commits.getValue(no))
                        }
                    }

                    // Test
                    asUserWithView(this).execute {
                        testCode(this)
                    }
                }
            }
        }
    }

    @Test
    fun `Change log based on Git property`() {
        doTest { branch ->
            // Getting the change log between build 5 and 7
            val data = run("""{
                branches(id: ${branch.id}) {
                    gitChangeLog(from: "5", to: "7") {
                        commits {
                            commit {
                                shortMessage
                            }
                        }
                    }
                }
            }""")
            val messages = data["branches"][0]["gitChangeLog"]["commits"].map {
                it["commit"]["shortMessage"].asText()
            }
            assertEquals(
                    listOf("Commit 7", "Commit 6"),
                    messages
            )
        }
    }

    @Test
    fun `Change log based on Git property using root query`() {
        doTest { branch ->
            // Gets the build ids
            val build5 = structureService.findBuildByName(
                    branch.project.name,
                    branch.name,
                    "5"
            ).orElse(null).id()
            val build7 = structureService.findBuildByName(
                    branch.project.name,
                    branch.name,
                    "7"
            ).orElse(null).id()
            // Getting the change log between build 5 and 7
            val data = run("""{
                gitChangeLog(from: $build5, to: $build7) {
                    commits {
                        commit {
                            shortMessage
                        }
                    }
                }
            }""")
            val messages = data["gitChangeLog"]["commits"].map {
                it["commit"]["shortMessage"].asText()
            }
            assertEquals(
                    listOf("Commit 7", "Commit 6"),
                    messages
            )
        }
    }

}
