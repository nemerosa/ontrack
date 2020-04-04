package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.BuildEdit
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StructureServiceIT : AbstractDSLTestSupport() {

    @Test
    fun `Project signature at creation`() {
        val e = doCreateProject()
        assertNotNull(e.signature)
        assertNotNull(e.signature.time)
        assertFalse(e.signature.user.isAnonymous)
    }

    @Test
    fun `Branch signature at creation`() {
        val e = doCreateBranch()
        assertNotNull(e.signature)
        assertNotNull(e.signature.time)
        assertFalse(e.signature.user.isAnonymous)
    }

    @Test
    fun `Promotion level signature at creation`() {
        val e = doCreatePromotionLevel()
        assertNotNull(e.signature)
        assertNotNull(e.signature.time)
        assertFalse(e.signature.user.isAnonymous)
    }

    @Test
    fun `Validation stamp signature at creation`() {
        val e = doCreateValidationStamp()
        assertNotNull(e.signature)
        assertNotNull(e.signature.time)
        assertFalse(e.signature.user.isAnonymous)
    }

    /**
     * Regression test for #76.
     *
     * When a build X (created before a build Y) is promoted after Y, this is still Y which should appear as
     * the last promotion.
     */
    @Test
    fun `#76 Make sure the promotion run order depends on the build and not the promotion run creation`() {

        // Creates a promotion level
        val promotionLevel = doCreatePromotionLevel()
        val branch = promotionLevel.branch

        // Creates two builds
        val build1 = doCreateBuild(branch, nd("1", "Build 1"))
        val build2 = doCreateBuild(branch, nd("2", "Build 2"))

        // Promotes build 2 BEFORE build 1
        doPromote(build2, promotionLevel, "Promotion of build 2")

        // Promotes build 1 AFTER build 2
        doPromote(build1, promotionLevel, "Promotion of build 1")

        // Gets the last promotion run for the promotion level
        val run = asUserWithView(branch).call {
            structureService.getLastPromotionRunForPromotionLevel(promotionLevel)
        }
        assertNotNull(run)
        assertEquals(build2.id, run.build.id, "Build 2 must be the last promoted")

    }

    @Test(expected = AccessDeniedException::class)
    fun `Changing a build signature is not granted by default`() {
        val build = doCreateBuild()
        // Attempts to change the build signature without being granted
        val time = TestUtils.dateTime()
        asUser().with(build, BuildEdit::class.java).call {
            structureService.saveBuild(build.withSignature(Signature.of(time, "Test2")))
        }
    }

    @Test
    fun `Changing a build signature can be granted`() {
        var build = doCreateBuild()
        // Changing the build signature
        val time = TestUtils.dateTime().plusDays(1)
        build = asUser().with(build, ProjectEdit::class.java).call {
            structureService.saveBuild(build.withSignature(Signature.of(time, "Test2")))
        }
        assertEquals("Test2", build.signature.user.name)
        assertEquals(time, build.signature.time)
    }

    @Test
    fun `#269 Branch name of 120 characters is allowed`() {
        val project = doCreateProject()
        doCreateBranch(project, nd(
                "b".repeat(120),
                "Test with 120 characters"
        ))
    }

    @Test(expected = DataIntegrityViolationException::class)
    fun `#269 Branch name of more than 120 characters is not allowed`() {
        val project = doCreateProject()
        doCreateBranch(project, nd(
                "b".repeat(121),
                "Test with 121 characters"
        ))
    }

    @Test
    fun `Project status view`() {
        // Creating branches
        val project = doCreateProject()
        (1..5).forEach { doCreateBranch(project, nd("1.0.${it}", "")) }
        // Gets the branch status views
        val views = asUserWithView(project).call { structureService.getBranchStatusViews(project.id) }
        assertEquals(5, views.size)
    }

    @Test
    fun `Previous build`() {
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", ""))
        val build2 = doCreateBuild(branch, nd("2", ""))
        // Gets the previous build of 2
        val o = asUserWithView(branch).call { structureService.getPreviousBuild(build2.id) }
        assertPresent(o)
        assertEquals(build1.id, o.get().id)
    }

    @Test
    fun `No previous build`() {
        val branch = doCreateBranch()
        val build = doCreateBuild(branch, nd("1", ""))
        // Gets the previous build of 1
        val o = asUserWithView(branch).call { structureService.getPreviousBuild(build.id) }
        assertNotPresent(o)
    }

    @Test
    fun `Next build`() {
        val branch = doCreateBranch()
        val build1 = doCreateBuild(branch, nd("1", ""))
        val build2 = doCreateBuild(branch, nd("2", ""))
        // Gets the next build of 1
        val o = asUserWithView(branch).call { structureService.getNextBuild(build1.id) }
        assertPresent(o)
        assertEquals(build2.id, o.get().id)
    }

    @Test
    fun `No next build`() {
        val branch = doCreateBranch()
        val build = doCreateBuild(branch, nd("1", ""))
        // Gets the next build of 1
        val o = asUserWithView(branch).call { structureService.getNextBuild(build.id) }
        assertNotPresent(o)
    }

    @Test
    fun `Safe pattern build search based on branch`() {
        val project = doCreateProject()
        val branch = doCreateBranch(project, nd("Branch 1", ""))
        val build = doCreateBuild(branch, nd("Build 1", ""))
        // Correct pattern
        var builds = asUser().withView(build).call {
            structureService.buildSearch(build.project.id, BuildSearchForm().withBranchName(".*1$"))
        }
        assertEquals(listOf(build.id), builds.map { it.id })
        // Incorrect pattern (unmatched parenthesis)
        builds = asUser().withView(build).call {
            structureService.buildSearch(build.project.id, BuildSearchForm().withBranchName(".*1)"))
        }
        assertTrue(builds.isEmpty(), "No match, but no failure")
    }

    @Test
    fun `Safe pattern build search based on build`() {
        val branch = doCreateBranch()
        val build = doCreateBuild(branch, nd("Build 1", ""))
        // Correct pattern
        var builds = asUser().withView(build).call {
            structureService.buildSearch(build.project.id, BuildSearchForm().withBuildName(".*1$"))
        }
        assertEquals(listOf(build.id), builds.map { it.id })
        // Incorrect pattern (unmatched parenthesis)
        builds = asUser().withView(build).call {
            structureService.buildSearch(build.project.id, BuildSearchForm().withBuildName(".*1)"))
        }
        assertTrue(builds.isEmpty(), "No match, but no failure")
    }

    @Test
    fun `Branches ordered in inverse chronological order`() {
        val project = doCreateProject()
        doCreateBranch(project, nd("2.0", ""))
        doCreateBranch(project, nd("2.1", ""))
        doCreateBranch(project, nd("1.0", ""))
        // Gets the list of branches
        val branches = asUserWithView(project).call {
            structureService.getBranchesForProject(project.id)
        }
        // Checks the order
        assertEquals(listOf("1.0", "2.1", "2.0"), branches.map { it.name })
    }

}
