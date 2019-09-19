package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.Test
import kotlin.test.assertEquals

class GitBranchModelMatcherProviderGraphQLIT : AbstractGitTestSupport() {

    @Test
    fun `List of project branches restricted to the default branching model for a Git project`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                gitProject(repo)
                branch("master") { gitBranch("master") }
                branch("develop") { gitBranch("develop") }
                branch("release-1.0") { gitBranch("release/1.0") }
                branch("feature-123-my-feature") { gitBranch("feature/123-my-feature") }
                // Gets the list of branches
                val data = asUserWithView(this).call {
                    run("""
                        {
                            projects(id: ${project.id}) {
                                branches(useModel: true) {
                                    name
                                }
                            }
                        }
                    """)
                }
                val names = data["projects"][0]["branches"].map { it["name"].asText() }
                // Checks the branches
                assertEquals(
                        setOf(
                                "master",
                                "develop",
                                "release-1.0"
                        ),
                        names.toSet()
                )
            }
        }
    }

    @Test
    fun `List of project branches restricted to a custom branching model for a Git project`() {
        createRepo {
            commits(1)
        } and { repo, _ ->
            project {
                gitProject(repo)
                setProperty(this, BranchingModelPropertyType::class.java,
                        BranchingModelProperty(listOf(
                                NameValue("Development", "master"),
                                NameValue("Release", "release/.*")
                        )
                        )
                )
                branch("master") { gitBranch("master") }
                branch("develop") { gitBranch("develop") }
                branch("release-1.0") { gitBranch("release/1.0") }
                branch("feature-123-my-feature") { gitBranch("feature/123-my-feature") }
                // Gets the list of branches
                val data = asUserWithView(this).call {
                    run("""
                        {
                            projects(id: ${project.id}) {
                                branches(useModel: true) {
                                    name
                                }
                            }
                        }
                    """)
                }
                val names = data["projects"][0]["branches"].map { it["name"].asText() }
                // Checks the branches
                assertEquals(
                        setOf(
                                "master",
                                "release-1.0"
                        ),
                        names.toSet()
                )
            }
        }
    }

    @Test
    fun `List of project branches unrestricted for a non-Git project`() {
        project {
            branch("master")
            branch("develop")
            branch("release-1.0")
            branch("feature-123-my-feature")
            // Gets the list of branches
            val data = asUserWithView(this).call {
                run("""
                        {
                            projects(id: ${project.id}) {
                                branches(useModel: true) {
                                    name
                                }
                            }
                        }
                    """)
            }
            val names = data["projects"][0]["branches"].map { it["name"].asText() }
            // Checks the branches
            assertEquals(
                    setOf(
                            "master",
                            "develop",
                            "release-1.0",
                            "feature-123-my-feature"
                    ),
                    names.toSet()
            )
        }
    }

}