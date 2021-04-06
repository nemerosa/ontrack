package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.test.assertJsonNotNull
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
    fun `Change log of issues based on Git property`() {
        this.createRepo {
            mapOf(
                1 to commit(1, "#1 Issue 1"),
                2 to commit(2, "#2 Issue 2"),
                3 to commit(3, "#1 Also issue 1"),
                4 to commit(4, "No issue"),
                5 to commit(5, "#1 #2 Both issues")
            )
        } and { repo, commits ->
            project {
                gitProject(repo, sync = true)
                branch {
                    gitBranch("master") {
                        commitAsProperty()
                    }

                    // Creates builds for each commits
                    (1..5).forEach { no ->
                        build(no.toString()) {
                            gitCommitProperty(commits.getValue(no))
                        }
                    }

                    // Test
                    asUserWithView(this).execute {
                        // Getting the change log between build 1 and 5
                        run("""{
                            branches(id: $id) {
                                gitChangeLog(from: "1", to: "5") {
                                    issues {
                                        issueServiceConfiguration {
                                            id
                                            name
                                            serviceId
                                        }
                                        list {
                                            issue {
                                                key
                                                displayKey
                                                summary
                                                url
                                                status {
                                                    name
                                                }
                                                updateTime
                                            }
                                        }
                                    }
                                }
                            }
                        }""").let { data ->
                            val issues = data.path("branches").path(0).path("gitChangeLog").path("issues")
                            assertJsonNotNull(issues) {
                                val issueServiceConfiguration = path("issueServiceConfiguration")
                                assertJsonNotNull(issueServiceConfiguration) {
                                    assertEquals("mock//default", issueServiceConfiguration.getTextField("id"))
                                    assertEquals("default (Mock issue)", issueServiceConfiguration.getTextField("name"))
                                    assertEquals("mock", issueServiceConfiguration.getTextField("serviceId"))
                                }
                                val list = path("list")
                                assertJsonNotNull(list) {
                                    assertEquals(2, list.size())
                                    list.forEach { item ->
                                        val issue = item.path("issue")
                                        assertJsonNotNull(issue) {
                                            val key = issue.getTextField("key").toInt()
                                            assertEquals("#$key", issue.getTextField("displayKey"))
                                            assertEquals("Issue #$key", issue.getTextField("summary"))
                                            assertEquals("uri:issue/$key", issue.getTextField("url"))
                                            assertEquals("OPEN", issue.path("status").getTextField("name"))
                                            assertJsonNotNull(issue.path("updateTime"))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
