package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitBranchConfigurationPropertyMutationProviderIT : AbstractGitTestSupport() {

    @Test
    fun `Setting the Git branch configuration by ID`() {
        createRepo {
            sequence(
                1, // Commit
                "release/1.0" // Creates branch
            )

        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    run("""
                        mutation {
                            setBranchGitConfigPropertyById(input: {id: $id, gitBranch: "release/1.0"}) {
                                branch {
                                    id
                                }
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        val node = assertNoUserError(data, "setBranchGitConfigPropertyById")
                        assertEquals(id(), node.path("branch").path("id").asInt())

                        assertNotNull(getProperty(this, GitBranchConfigurationPropertyType::class.java)) {
                            assertEquals("release/1.0", it.branch)
                            assertNotNull(it.buildCommitLink) { link ->
                                assertEquals("git-commit-property", link.id)
                            }
                            assertEquals(0, it.buildTagInterval)
                            assertEquals(false, it.isOverride)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Setting the Git branch configuration by name`() {
        createRepo {
            sequence(
                1, // Commit
                "release/1.0" // Creates branch
            )

        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    run("""
                        mutation {
                            setBranchGitConfigProperty(input: {project: "${project.name}", branch: "$name", gitBranch: "release/1.0"}) {
                                branch {
                                    id
                                }
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        val node = assertNoUserError(data, "setBranchGitConfigProperty")
                        assertEquals(id(), node.path("branch").path("id").asInt())

                        assertNotNull(getProperty(this, GitBranchConfigurationPropertyType::class.java)) {
                            assertEquals("release/1.0", it.branch)
                            assertNotNull(it.buildCommitLink) { link ->
                                assertEquals("git-commit-property", link.id)
                            }
                            assertEquals(0, it.buildTagInterval)
                            assertEquals(false, it.isOverride)
                        }
                    }
                }
            }
        }
    }

}