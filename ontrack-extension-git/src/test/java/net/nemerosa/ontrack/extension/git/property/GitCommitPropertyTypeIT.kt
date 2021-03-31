package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNotNull

class GitCommitPropertyTypeIT : AbstractGitTestSupport() {

    @Test
    fun `Build filter on commit`() {
        createRepo {
            commits(5)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    (1..5).forEach { index ->
                        build(index.toString()) {
                            gitCommitProperty(commits.getValue(index))
                        }
                    }
                    // Looks for commit n°3
                    val builds = buildFilterService.standardFilterProviderData(1)
                        .withWithProperty(GitCommitPropertyType::class.java.name)
                        .withWithPropertyValue(commits.getValue(3))
                        .build()
                        .filterBranchBuilds(this)
                    assertEquals(
                        listOf("3"),
                        builds.map { it.name }
                    )
                }
            }
        }
    }

    @Test
    fun `Build project search on commit`() {
        createRepo {
            commits(5)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    (1..5).forEach { index ->
                        build(index.toString()) {
                            gitCommitProperty(commits.getValue(index))
                        }
                    }
                    // Looks for commit n°3 at project level
                    val form = BuildSearchForm()
                        .withProperty(GitCommitPropertyType::class.java.name)
                        .withPropertyValue(commits.getValue(3))
                    val builds = structureService.buildSearch(project.id, form)
                    assertEquals(
                        listOf("3"),
                        builds.map { it.name }
                    )
                }
            }
        }
    }

    @Test
    fun onPropertyChanged() {
        createRepo {
            commits(1)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    build("1") {
                        gitCommitProperty(commits.getValue(1))
                        // Checks that we can now get a GitCommit for this build
                        val commit = gitService.getCommitForBuild(this)
                        assertNotNull(commit) {
                            assertEquals("Commit 1", it.commit.shortMessage)
                        }
                    }
                }
            }
        }
    }
}