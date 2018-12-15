package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

/**
 * Integration tests for Git support.
 */
class GitChangeLogIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitService: GitService

    @Test
    fun `Change log based on commits`() {
        createRepo {
            // Creates a Git repository with 10 commits
            commits(10)
        } and { repo, commits: Map<Int, String> ->
            // Creates a project ...
            project project@{
                gitProject(repo)
                // ...  & the branch with a link based on commits
                branch branch@{
                    gitBranch {
                        buildNameAsCommit(abbreviated = false)
                    }
                    // Creates builds for some commits
                    build(commits.getOrFail(2))
                    build(commits.getOrFail(5))
                    build(commits.getOrFail(7))
                    build(commits.getOrFail(8))

                    // Getting the change log between build 5 and 7
                    asUserWithView(this@branch).execute {
                        val buildFrom = structureService.findBuildByName(
                                this@project.name,
                                this@branch.name,
                                commits.getOrFail(5)
                        ).get()
                        val buildTo = structureService.findBuildByName(
                                this@project.name,
                                this@branch.name,
                                commits.getOrFail(7)
                        ).get()
                        val buildDiffRequest = BuildDiffRequest(
                                buildFrom.id,
                                buildTo.id
                        )
                        val changeLog = gitService.changeLog(buildDiffRequest)
                        // Getting the commits
                        val changeLogCommits = gitService.getChangeLogCommits(changeLog)
                        // Checks the commits
                        val messages = changeLogCommits.log.commits.map { it.commit.shortMessage }
                        assertEquals(
                                listOf("Commit 7", "Commit 6"),
                                messages
                        )

                    }
                }
            }
        }
    }

    @Test
    fun `Change log based on tags`() {

        createRepo {
            // Creates a Git repository with 10 commits and some tags
            sequence(
                    1,
                    2 to "v2",
                    3..4,
                    5 to "v5",
                    6,
                    7 to "v7",
                    8 to "v8",
                    9..10
            )
        } and { repo, commits: Map<Int, String> ->
            // Creates a project ...
            project project@{
                gitProject(repo)
                // ...  & the branch with a link based on tag name
                branch branch@{
                    gitBranch {
                        tagBuildName()
                    }
                    // Creates builds for some tags
                    listOf(2, 5, 7, 8).forEach {
                        build("v$it")
                    }

                    // Getting the change log between build 5 and 7
                    asUserWithView(this@branch).execute {
                        val buildFrom = structureService.findBuildByName(
                                this@project.name,
                                this@branch.name,
                                "v5"
                        ).get()
                        val buildTo = structureService.findBuildByName(
                                this@project.name,
                                this@branch.name,
                                "v7"
                        ).get()
                        val buildDiffRequest = BuildDiffRequest(
                                buildFrom.id,
                                buildTo.id
                        )
                        val changeLog = gitService.changeLog(buildDiffRequest)
                        // Getting the commits
                        val changeLogCommits = gitService.getChangeLogCommits(changeLog)
                        // Checks the commits
                        val messages = changeLogCommits.log.commits.map { it.commit.shortMessage }
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
