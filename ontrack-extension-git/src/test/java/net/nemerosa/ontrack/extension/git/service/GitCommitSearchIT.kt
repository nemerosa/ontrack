package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import org.junit.jupiter.api.Test

class GitCommitSearchIT : AbstractGitTestSupport() {

    @Test
    fun `Commit on one branch with commit property`() {
        createRepo {
            commits(10, pauses = true)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    // Creates some builds on this branch
                    build(1, commits)
                    build(3, commits)
                    build(5, commits)
                    build(9, commits)
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    assertBranchInfos(
                            "Development" to listOf(
                                    BranchInfoTest(
                                            branch = "main",
                                            firstBuild = "3"
                                    )
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Commit on two branches with commit property`() {
        createRepo {
            sequenceWithPauses(
                    (1..3),
                    "release/2.0",
                    4,
                    "main",
                    5,
                    "release/2.0",
                    (6..7),
                    "main",
                    (8..10)
            )
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    // Creates some builds on this branch
                    build(1, commits, listOf(test1))
                    build(3, commits, listOf(test1, test2), listOf(silver))
                    build(5, commits)
                    build(9, commits, listOf(test1, test2), listOf(silver))
                }
                branch("release-2.0") {
                    gitBranch("release/2.0") {
                        commitAsProperty()
                    }
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    // Creates some builds on this branch
                    build(4, commits, listOf(test1))
                    build(8, commits, listOf(test1, test2), listOf(silver, gold))
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    assertBranchInfos(
                            "Development" to listOf(
                                    BranchInfoTest(
                                            branch = "main",
                                            firstBuild = "3",
                                            promotions = listOf(
                                                    "SILVER" to "3"
                                            )
                                    )
                            ),
                            "Releases" to listOf(
                                    BranchInfoTest(
                                            branch = "release-2.0",
                                            firstBuild = "4",
                                            promotions = listOf(
                                                    "SILVER" to "8",
                                                    "GOLD" to "8"
                                            )
                                    )
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Commit on two branches with commit property but one branch is disabled`() {
        createRepo {
            sequenceWithPauses(
                    (1..3),
                    "release/2.0",
                    4,
                    "main",
                    5,
                    "release/2.0",
                    (6..7),
                    "main",
                    (8..10)
            )
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        commitAsProperty()
                    }
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    // Creates some builds on this branch
                    build(1, commits, listOf(test1))
                    build(3, commits, listOf(test1, test2), listOf(silver))
                    build(5, commits)
                    build(9, commits, listOf(test1, test2), listOf(silver))
                }
                branch("release-2.0") {
                    gitBranch("release/2.0") {
                        commitAsProperty()
                    }
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    // Creates some builds on this branch
                    build(4, commits, listOf(test1), listOf(silver))
                    build(8, commits, listOf(test1, test2), listOf(silver, gold))
                    // Disables the branch
                    asAdmin().execute {
                        structureService.disableBranch(this)
                    }
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    assertBranchInfos(
                            "Development" to listOf(
                                    BranchInfoTest(
                                            branch = "main",
                                            firstBuild = "3",
                                            promotions = listOf(
                                                    "SILVER" to "3"
                                            )
                                    )
                            ),
                            "Releases" to listOf(
                                    BranchInfoTest(
                                            branch = "release-2.0",
                                            firstBuild = "4",
                                            promotions = listOf(
                                                    "SILVER" to "4",
                                                    "GOLD" to "8"
                                            )
                                    )
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with long commit id as build name`() {
        createRepo {
            commits(10, pauses = true)
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        buildNameAsCommit(abbreviated = false)
                    }
                    // Creates some builds on this branch
                    build(commits.getValue(1))
                    build(commits.getValue(3))
                    build(commits.getValue(5))
                    build(commits.getValue(9))
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    "Development" to listOf(
                            BranchInfoTest(
                                    branch = "main",
                                    firstBuild = "3"
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with short commit id as build name`() {
        createRepo {
            commits(9, pauses = true)
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        buildNameAsCommit(abbreviated = true)
                    }
                    // Creates some builds on this branch
                    build(repo.commitLookup("Commit 1"))
                    build(repo.commitLookup("Commit 3"))
                    build(repo.commitLookup("Commit 5"))
                    build(repo.commitLookup("Commit 9"))
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    "Development" to listOf(
                            BranchInfoTest(
                                    branch = "main",
                                    firstBuild = "3"
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with tag build name property`() {
        createRepo {
            sequenceWithPauses(
                    (1..3),
                    4 to "1.0.0",
                    5,
                    6 to "1.0.1",
                    (7..8),
                    9 to "1.0.2",
                    10
            )
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        tagBuildName()
                    }
                    // Creates some builds on this branch, using the tags above
                    build("1.0.0")
                    build("1.0.1")
                    build("1.0.2")
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 5) {
                    "Development" to listOf(
                            BranchInfoTest(
                                    branch = "main",
                                    firstBuild = "3"
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with tag pattern build name property`() {
        createRepo {
            sequenceWithPauses(
                    (1..3),
                    4 to "1.0.0",
                    5,
                    6 to "test",
                    (7..8),
                    9 to "1.0.2",
                    10
            )
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch("main") {
                    gitBranch("main") {
                        tagPatternBuildName("1.0.*")
                    }
                    // Creates some builds on this branch, using the tags above
                    build("1.0.0")
                    build("1.0.2")
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 5) {
                    "Development" to listOf(
                            BranchInfoTest(
                                    branch = "main",
                                    firstBuild = "3"
                            )
                    )
                }
            }
        }
    }

}