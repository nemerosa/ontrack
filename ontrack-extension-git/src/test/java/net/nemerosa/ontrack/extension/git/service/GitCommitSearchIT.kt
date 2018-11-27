package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.model.OntrackGitCommitInfo
import net.nemerosa.ontrack.git.support.GitRepo
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
        lateinit var commits: Map<Int, String>
        GitRepo.prepare {
            // Creates 10 commits
            gitInit()
            commits = commits(10)
            log()
        } withClone { _, clientRepo, _ ->
            project {
                gitProject(clientRepo)
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
        assertEquals("Commit $no", info.uiCommit.fullAnnotatedMessage)
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
                buildView.validationStampRunViews.map { it.validationStamp.name }.toSet()
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