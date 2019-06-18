package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Integration tests for Git support.
 */
class GitChangeLogIT : AbstractGitTestSupport() {

    @Test
    fun `Change log issues IDs`() {
        createRepo {
            mapOf(
                    1 to commit(1, "#1 Issue 1"),
                    2 to commit(2, "#2 Issue 2"),
                    3 to commit(3, "#1 Also issue 1"),
                    4 to commit(4, "No issue"),
                    5 to commit(5, "#1 #2 Both issues")
            )
        } and { repo, commits: Map<Int, String> ->
            // Creates a project for this repo
            project {
                gitProject(repo)
                // ...  & the branch with a link based on commit properties
                branch {
                    gitBranch {
                        commitAsProperty()
                    }
                    // Creates builds for the commits
                    val builds = (1..5).associate {
                        it to build(name = it.toString()) {
                            gitCommitProperty(commits.getValue(it))
                        }
                    }
                    // Getting the change log between 1 and 5
                    asUserWithView(this) {
                        // Lower build
                        val buildFrom = builds.getValue(1)
                        val buildTo = builds.getValue(5)
                        val buildDiffRequest = BuildDiffRequest(
                                buildFrom.id,
                                buildTo.id
                        )
                        val changeLog = gitService.changeLog(buildDiffRequest)
                        // Gets the issues IDs
                        val issues = gitService.getChangeLogIssuesIds(changeLog)
                        // Checks the issues
                        assertEquals(
                                listOf("1", "2"),
                                issues
                        )
                    }
                }
            }
        }
    }

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
        } and { repo, _ ->
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
