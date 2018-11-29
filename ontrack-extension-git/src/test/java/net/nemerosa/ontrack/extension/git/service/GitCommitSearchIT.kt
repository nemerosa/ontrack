package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.model.OntrackGitCommitInfo
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

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
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    // Creates some builds on this branch
                    build(1, commits, listOf(test1))
                    build(3, commits, listOf(test1, test2), listOf(silver))
                    build(5, commits)
                    build(9, commits, listOf(test1, test2), listOf(silver, gold))
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    assertCountBuildViews(1)
                    buildViewTest(0, "3", setOf("Test1", "Test2"), setOf("SILVER"))
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
                    assertCountBuildViews(2)
                    buildViewTest(0, "3", setOf("Test1", "Test2"), setOf("SILVER"))
                    buildViewTest(1, "4", setOf("Test1"))
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
                    assertCountBuildViews(1)
                    buildViewTest(0, "3", setOf("Test1", "Test2"), setOf("SILVER"))
                }
            }
        }
    }

    @Test
    fun `Commit on two branches with long commit id as build name`() {
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
                        buildNameAsCommit(abbreviated = false)
                    }
                    // Creates some builds on this branch
                    build(commits.getOrFail(1))
                    build(commits.getOrFail(3))
                    build(commits.getOrFail(5))
                    build(commits.getOrFail(9))
                }
                branch("release-2.0") {
                    gitBranch("release/2.0") {
                        buildNameAsCommit(abbreviated = false)
                    }
                    // Creates some builds on this branch
                    build(commits.getOrFail(4))
                    build(commits.getOrFail(8))
                }
                // Tests for commit 2
                commitInfoTest(this, commits, 2) {
                    assertCountBuildViews(2)
                    buildViewTest(0, commits.getOrFail(3))
                    buildViewTest(1, commits.getOrFail(4))
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

    private fun OntrackGitCommitInfo.assertCountBuildViews(count: Int) {
        assertEquals(count, buildViews.size, "Number of build views must be $count")
    }

    private fun OntrackGitCommitInfo.buildViewTest(
            index: Int,
            buildName: String,
            validations: Set<String> = emptySet(),
            promotions: Set<String> = emptySet()
    ) {
        val buildView = buildViews.toList()[index]
        assertEquals(buildName, buildView.build.name)
        assertEquals(
                validations,
                buildView.validationStampRunViews
                        .filterNot { it.validationRun.isEmpty() }
                        .map { it.validationStamp.name }
                        .toSet()
        )
        assertEquals(
                promotions,
                buildView.promotionRuns.map { it.promotionLevel.name }.toSet()
        )
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