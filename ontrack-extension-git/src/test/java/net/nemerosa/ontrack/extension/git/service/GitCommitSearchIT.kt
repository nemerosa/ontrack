package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.model.BranchInfo
import net.nemerosa.ontrack.extension.git.model.OntrackGitCommitInfo
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GitCommitSearchIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitService: GitService

    @Test
    fun `Commit on one branch with commit property`() {
        createRepo {
            commits(10)
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
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
                    assertFirstBuild("3")
                    assertNoBranchInfos()
                }
            }
        }
    }

    @Test
    fun `Commit on two branches with commit property`() {
        createRepo {
            sequence(
                    (1..3),
                    "release/2.0",
                    4,
                    "master",
                    5,
                    "release/2.0",
                    (6..7),
                    "master",
                    (8..10)
            )
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
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
                    assertFirstBuild("3")
                    assertBranchInfos(
                            "Releases" to listOf(
                                    BranchInfoTest(
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
            sequence(
                    (1..3),
                    "release/2.0",
                    4,
                    "master",
                    5,
                    "release/2.0",
                    (6..7),
                    "master",
                    (8..10)
            )
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
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
                    // Disables the branch
                    asAdmin().execute {
                        structureService.disableBranch(this)
                    }
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    // TODO assertCountBuildViews(1)
                    // TODO buildViewTest(0, "3", setOf("Test1", "Test2"), setOf("SILVER"))
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with long commit id as build name`() {
        createRepo {
            commits(10)
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
                        buildNameAsCommit(abbreviated = false)
                    }
                    // Creates some builds on this branch
                    build(commits.getOrFail(1))
                    build(commits.getOrFail(3))
                    build(commits.getOrFail(5))
                    build(commits.getOrFail(9))
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    // TODO assertCountBuildViews(1)
                    // TODO buildViewTest(0, commits.getOrFail(3))
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with short commit id as build name`() {
        createRepo {
            commits(9)
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
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
                    // TODO assertCountBuildViews(1)
                    // TODO buildViewTest(0, repo.commitLookup("Commit 3"))
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with tag build name property`() {
        createRepo {
            sequence(
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
                branch("master") {
                    gitBranch("master") {
                        tagBuildName()
                    }
                    // Creates some builds on this branch, using the tags above
                    build("1.0.0")
                    build("1.0.1")
                    build("1.0.2")
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 5) {
                    // TODO assertCountBuildViews(1)
                    // TODO buildViewTest(0, "1.0.1")
                }
            }
        }
    }

    @Test
    fun `Commit on one branch with tag pattern build name property`() {
        createRepo {
            sequence(
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
                branch("master") {
                    gitBranch("master") {
                        tagPatternBuildName("1.0.*")
                    }
                    // Creates some builds on this branch, using the tags above
                    build("1.0.0")
                    build("1.0.2")
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 5) {
                    // TODO assertCountBuildViews(1)
                    // TODO buildViewTest(0, "1.0.2")
                }
            }
        }
    }

    private fun commitInfoTest(
            project: Project,
            commits: Map<Int, String>,
            no: Int,
            tests: OntrackGitCommitInfo.() -> Unit
    ) {
        val commit = commits.getOrFail(no)
        val info = gitService.getCommitProjectInfo(project.id, commit)
        // Commit message & hash
        assertEquals(commit, info.uiCommit.id)
        assertEquals("Commit $no", info.uiCommit.annotatedMessage)
        // Tests
        info.tests()
    }

    private fun OntrackGitCommitInfo.assertFirstBuild(expectedName: String) {
        assertNotNull(firstBuild) {
            assertEquals(expectedName, it.name)
        }
    }

    private fun OntrackGitCommitInfo.assertNoBranchInfos() {
        assertTrue(branchInfos.isEmpty(), "No branch infos being found")
    }

    private fun OntrackGitCommitInfo.assertBranchInfos(
            vararg tests: Pair<String, List<BranchInfoTest>>
    ) {
        assertEquals(tests.size, branchInfos.size, "Number of tests must match the number of collected branch infos")
        // Test per test
        tests.forEach { (type, branchInfoTests) ->
            val branchInfoList = branchInfos[type]
            assertNotNull(branchInfoList) { it ->
                assertEquals(branchInfoTests.size, it.size, "Number of tests for type $type must match the number of collect branch infos")
                // Group by pair
                it.zip(branchInfoTests).forEach { (branchInfo, branchInfoTest) ->
                    branchInfoTest(branchInfo)
                }
            }
        }
    }

    private class BranchInfoTest(
            private val firstBuild: String?,
            private val promotions: List<Pair<String, String>>
    ) {
        operator fun invoke(branchInfo: BranchInfo) {
            // First build test
            if (firstBuild != null) {
                assertNotNull(branchInfo.firstBuild, "First build expected") {
                    assertEquals(firstBuild, it.name)
                }
            } else {
                assertNull(branchInfo.firstBuild, "No first build")
            }
            // Promotion tests
            assertEquals(promotions.size, branchInfo.promotions.size)
            branchInfo.promotions.zip(promotions).forEach { (run, promotionTest) ->
                val promotion = promotionTest.first
                val name = promotionTest.second
                assertEquals(promotion, run.promotionLevel.name)
                assertEquals(name, run.build.name)
            }
        }
    }

    private fun Branch.build(
            no: Int,
            commits: Map<Int, String>,
            validations: List<ValidationStamp> = emptyList(),
            promotions: List<PromotionLevel> = emptyList()
    ) {
        build(no.toString()) {
            gitCommitProperty(commits.getOrFail(no))
            validations.forEach { validate(it) }
            promotions.forEach { promote(it) }
        }
    }

}