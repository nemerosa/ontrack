package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.BuildEdit
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.AccessDeniedException

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class StructureServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService

    /**
     * Regression test for #76.
     *
     * When a build X (created before a build Y) is promoted after Y, this is still Y which should appear as
     * the last promotion.
     */
    @Test
    void '#76 Make sure the promotion run order depends on the build and not the promotion run creation'() {

        // Creates a promotion level
        def promotionLevel = doCreatePromotionLevel()
        def branch = promotionLevel.branch

        // Creates two builds
        def build1 = doCreateBuild(branch, nd("1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("2", "Build 2"))

        // Promotes build 2 BEFORE build 1
        doPromote(build2, promotionLevel, "Promotion of build 2")

        // Promotes build 1 AFTER build 2
        doPromote(build1, promotionLevel, "Promotion of build 1")

        // Gets the last promotion run for the promotion level
        def run = asUser().with(branch.projectId(), ProjectView).call({
            structureService.getLastPromotionRunForPromotionLevel(promotionLevel)
        })
        assert run != null
        assert run.build.id == build2.id: "Build 2 must be the last promoted"

    }

    @Test(expected = AccessDeniedException)
    void 'Changing a build signature is not granted by default'() {
        def build = doCreateBuild()
        // Attempts to change the build signature without being granted
        def time = TestUtils.dateTime()
        asUser().with(build, BuildEdit).call {
            structureService.saveBuild(build.withSignature(Signature.of(time, "Test2")))
        }
    }

    @Test
    void 'Changing a build signature can be granted'() {
        def build = doCreateBuild()
        // Changing the build signature
        def time = TestUtils.dateTime().plusDays(1)
        build = asUser().with(build, ProjectEdit).call {
            structureService.saveBuild(build.withSignature(Signature.of(time, "Test2")))
        }
        assert build.signature.user.name == 'Test2'
        assert build.signature.time == time
    }

    @Test
    void '#269 Branch name of 120 characters is allowed'() {
        def project = doCreateProject()
        doCreateBranch(project, nd(
                'b' * 120,
                'Test with 120 characters'
        ))
    }

    @Test(expected = DataIntegrityViolationException)
    void '#269 Branch name of more than 120 characters is not allowed'() {
        def project = doCreateProject()
        doCreateBranch(project, nd(
                'b' * 121,
                'Test with 120 characters'
        ))
    }

    @Test
    void 'Project status view'() {
        // Creating branches
        def project = doCreateProject()
        (1..5).each { doCreateBranch(project, nd("1.0.${it}", "")) }
        // Gets the branch status views
        def views = asUser().with(project, ProjectView).call { structureService.getBranchStatusViews(project.id) }
        assert views.size() == 5
    }

    @Test
    void 'Previous build'() {
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1", ""))
        def build2 = doCreateBuild(branch, nd("2", ""))
        // Gets the previous build of 2
        def o = asUser().with(branch, ProjectView).call { structureService.getPreviousBuild(build2.id) }
        assert o.present
        assert o.get().id == build1.id
    }

    @Test
    void 'No previous build'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, nd("1", ""))
        // Gets the previous build of 1
        def o = asUser().with(branch, ProjectView).call { structureService.getPreviousBuild(build.id) }
        assert !o.present
    }

    @Test
    void 'Next build'() {
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1", ""))
        def build2 = doCreateBuild(branch, nd("2", ""))
        // Gets the next build of 1
        def o = asUser().with(branch, ProjectView).call { structureService.getNextBuild(build1.id) }
        assert o.present
        assert o.get().id == build2.id
    }

    @Test
    void 'No next build'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, nd("1", ""))
        // Gets the next build of 1
        def o = asUser().with(branch, ProjectView).call { structureService.getNextBuild(build.id) }
        assert !o.present
    }

    @Test
    void 'Safe pattern build search based on branch'() {
        def project = doCreateProject()
        def branch = doCreateBranch(project, nd('Branch 1', ''))
        def build = doCreateBuild(branch, nd('Build 1', ''))
        // Correct pattern
        def builds = asUser().withView(build).call { structureService.buildSearch(build.project.id, new BuildSearchForm().withBranchName('.*1$')) }
        assert builds*.id == [build.id]
        // Incorrect pattern (unmatched parenthesis)
        builds = asUser().withView(build).call { structureService.buildSearch(build.project.id, new BuildSearchForm().withBranchName('.*1)')) }
        assert builds.empty: "No match, but no failure"
    }

    @Test
    void 'Safe pattern build search based on build'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, nd('Build 1', ''))
        // Correct pattern
        def builds = asUser().withView(build).call { structureService.buildSearch(build.project.id, new BuildSearchForm().withBuildName('.*1$')) }
        assert builds*.id == [build.id]
        // Incorrect pattern (unmatched parenthesis)
        builds = asUser().withView(build).call { structureService.buildSearch(build.project.id, new BuildSearchForm().withBuildName('.*1)')) }
        assert builds.empty: "No match, but no failure"
    }

}
