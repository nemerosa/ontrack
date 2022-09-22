package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.extension.git.AbstractGitTestJUnit4Support
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitCommitPropertyMutationProviderGraphQLIT : AbstractGitTestJUnit4Support() {

    @Test
    fun `Setting the Git build commit by ID`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    build {
                        run("""
                            mutation {
                                setBuildGitCommitPropertyById(input: {id: $id, commit: "${commits[1]}"}) {
                                    build {
                                        id
                                    }
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            val node = assertNoUserError(data, "setBuildGitCommitPropertyById")
                            assertEquals(id(), node.path("build").path("id").asInt())

                            assertNotNull(getProperty(this, GitCommitPropertyType::class.java)) {
                                assertEquals(commits[1], it.commit)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Setting the Git build commit by name`() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    build {
                        run("""
                            mutation {
                                setBuildGitCommitProperty(input: {project: "${project.name}", branch: "${branch.name}", build: "$name", commit: "${commits[1]}"}) {
                                    build {
                                        id
                                    }
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            val node = assertNoUserError(data, "setBuildGitCommitProperty")
                            assertEquals(id(), node.path("build").path("id").asInt())

                            assertNotNull(getProperty(this, GitCommitPropertyType::class.java)) {
                                assertEquals(commits[1], it.commit)
                            }
                        }
                    }
                }
            }
        }
    }

}