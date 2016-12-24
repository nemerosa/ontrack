package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.security.BuildConfig
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.BuildEdit
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

class BuildLinkIT extends AbstractServiceTestSupport {

    @Autowired
    private BuildFilterService buildFilterService

    @Test(expected = ProjectNotFoundException)
    void 'Edition of links - project not found at all'() {
        def source = doCreateBuild()
        asUser().with(source, BuildConfig).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            new BuildLinkFormItem(uid('P'), 'xxx'),
                    ])
            )
        }
    }

    @Test(expected = ProjectNotFoundException)
    void 'Edition of links - project not authorised'() {
        grantViewToAll false
        def source = doCreateBuild()
        def target = doCreateBuild()
        asUser().with(source, BuildConfig).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            new BuildLinkFormItem(target.project.name, target.name),
                    ])
            )
        }
    }

    @Test(expected = BuildNotFoundException)
    void 'Edition of links - build not found'() {
        def source = doCreateBuild()
        def target = doCreateProject()
        asUser().with(source, BuildConfig).withView(target).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            new BuildLinkFormItem(target.name, 'xxx'),
                    ])
            )
        }
    }

    @Test
    void 'Edition of links - full rights - adding one link'() {
        def source = doCreateBuild()
        def target1 = doCreateBuild()
        def target2 = doCreateBuild()
        def target3 = doCreateBuild()
        asUser().with(source, BuildConfig).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target2).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            new BuildLinkFormItem(target1.project.name, target1.name), // Existing
                            new BuildLinkFormItem(target2.project.name, target2.name), // Existing
                            new BuildLinkFormItem(target3.project.name, target3.name), // New
                    ])
            )
            // Checks all builds are still linked
            assert [target1.id, target2.id, target3.id] as Set == structureService.getBuildLinksFrom(source)*.id as Set
        }
    }

    @Test
    void 'Edition of links - full rights - adding and removing'() {
        def source = doCreateBuild()
        def target1 = doCreateBuild()
        def target2 = doCreateBuild()
        def target3 = doCreateBuild()
        asUser().with(source, BuildConfig).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target2).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            new BuildLinkFormItem(target1.project.name, target1.name), // Existing
                            // new BuildLinkFormItem(target2.project.name, target2.name), // Removing
                            new BuildLinkFormItem(target3.project.name, target3.name), // New
                    ])
            )
            // Checks all builds are still linked
            assert [target1.id, target3.id] as Set == structureService.getBuildLinksFrom(source)*.id as Set
        }
    }

    @Test
    void 'Edition of links - full rights - adding only'() {
        def source = doCreateBuild()
        def target1 = doCreateBuild()
        def target2 = doCreateBuild()
        def target3 = doCreateBuild()
        asUser().with(source, BuildConfig).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target2).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(true, [
                            new BuildLinkFormItem(target3.project.name, target3.name), // New
                    ])
            )
            // Checks all builds are still linked
            assert [target1.id, target2.id, target3.id] as Set == structureService.getBuildLinksFrom(source)*.id as Set
        }
    }

    @Test
    void 'Edition of links - partial rights - adding one link'() {
        grantViewToAll false
        def source = doCreateBuild()
        def target1 = doCreateBuild()
        def target2 = doCreateBuild()
        def target3 = doCreateBuild()
        asUser().with(source, BuildConfig).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            new BuildLinkFormItem(target1.project.name, target1.name), // Existing
                            new BuildLinkFormItem(target3.project.name, target3.name), // New
                    ])
            )
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target2).withView(target3).call {
            // Checks all builds are still linked
            assert [target1.id, target2.id, target3.id] as Set == structureService.getBuildLinksFrom(source)*.id as Set
        }
    }

    @Test
    void 'Edition of links - partial rights - adding and removing'() {
        grantViewToAll false
        def source = doCreateBuild()
        def target1 = doCreateBuild()
        def target2 = doCreateBuild()
        def target3 = doCreateBuild()
        asUser().with(source, BuildConfig).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(false, [
                            // new BuildLinkFormItem(target1.project.name, target1.name), // Removing
                            new BuildLinkFormItem(target3.project.name, target3.name), // New
                    ])
            )
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target2).withView(target3).call {
            // Checks all builds are still linked
            assert [target2.id, target3.id] as Set == structureService.getBuildLinksFrom(source)*.id as Set
        }
    }

    @Test
    void 'Edition of links - partial rights - adding only'() {
        def source = doCreateBuild()
        def target1 = doCreateBuild()
        def target2 = doCreateBuild()
        def target3 = doCreateBuild()
        asUser().with(source, BuildConfig).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    new BuildLinkForm(true, [
                            new BuildLinkFormItem(target3.project.name, target3.name), // New
                    ])
            )
        }
        asUser().with(source, BuildConfig).withView(target1).withView(target2).withView(target3).call {
            // Checks all builds are still linked
            assert [target1.id, target2.id, target3.id] as Set == structureService.getBuildLinksFrom(source)*.id as Set
        }
    }

    @Test
    void 'Automation role can create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asGlobalRole("AUTOMATION").call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test(expected = AccessDeniedException)
    void 'Build config is needed on source build to create a link'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().withView(target).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test(expected = AccessDeniedException)
    void 'Build view is needed on target build to create a link'() {
        grantViewToAll false
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    void 'Adding and deleting a build'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
        // Deleting the build
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.deleteBuildLink(build, target)
        }
        // The build link is deleted
        targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert targets.empty
    }

    @Test
    void 'Adding twice a build'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
            // ... twice
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.size() == 1
        assert targets.find { it.name == target.name }
    }

    @Test
    void 'Controller role can create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asGlobalRole("CONTROLLER").call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test(expected = AccessDeniedException)
    void 'Creator role cannot create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asGlobalRole("CREATOR").call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    void 'Build config function grants access to create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test
    void 'Build edit function grants access to create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildEdit).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test(expected = AccessDeniedException)
    void 'Build create function does not grant access to create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildCreate).withView(target).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    void 'Testing the links'() {
        // Creates all builds
        def b1 = doCreateBuild()
        def b2 = doCreateBuild()
        def t1 = doCreateBuild()
        def t2 = doCreateBuild()
        // Creates the links
        asUser().with(b1, BuildConfig).withView(t1).withView(t2).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b1, t2)
        }
        asUser().with(b2, BuildConfig).withView(t2).call {
            structureService.addBuildLink(b2, t2)
        }
        // With full rights
        asUserWithView(b1, b2, t1, t2).call {
            assert structureService.isLinkedTo(b1, t1.project.name, '')
            assert structureService.isLinkedTo(b1, t2.project.name, '')
            assert structureService.isLinkedTo(b1, t1.project.name, t1.name)
            assert structureService.isLinkedTo(b1, t2.project.name, t2.name)
            assert structureService.isLinkedTo(b1, t1.project.name, t1.name.substring(0, 5) + '*')
            assert structureService.isLinkedTo(b1, t2.project.name, t2.name.substring(0, 5) + '*')

            assert structureService.isLinkedFrom(t2, b1.project.name, '')
            assert structureService.isLinkedFrom(t2, b1.project.name, b1.name)
            assert structureService.isLinkedFrom(t2, b1.project.name, b1.name.substring(0, 5) + '*')

            assert structureService.isLinkedFrom(t2, b2.project.name, '')
            assert structureService.isLinkedFrom(t2, b2.project.name, b2.name)
            assert structureService.isLinkedFrom(t2, b2.project.name, b2.name.substring(0, 5) + '*')
        }
    }

    @Test
    void 'Filter on linked to'() {
        // Source branch
        def source = doCreateBranch()
        def b1 = doCreateBuild(source, nd('1.0.0', ''))
        def b2 = doCreateBuild(source, nd('1.1.0', ''))
        // Target branch
        def target = doCreateBranch()
        def t1 = doCreateBuild(target, nd('2.0.0', ''))
        def t2 = doCreateBuild(target, nd('2.1.0', ''))
        // Creates links
        asUser().withView(target).with(source, BuildConfig).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertBuildLinkedToFilter source, target, target.project.name, [b2, b1]
        // Standard filter on project and all builds
        assertBuildLinkedToFilter source, target, "${target.project.name}:", [b2, b1]
        // Standard filter on project and all builds, using *
        assertBuildLinkedToFilter source, target, "${target.project.name}:*", [b2, b1]
        // Standard filter on project and common build prefix
        assertBuildLinkedToFilter source, target, "${target.project.name}:2*", [b2, b1]
        // Standard filter on project and prefix for one
        assertBuildLinkedToFilter source, target, "${target.project.name}:2.0*", [b1]
        // Standard filter on project and exact build
        assertBuildLinkedToFilter source, target, "${target.project.name}:2.0.0", [b1]
    }

    private void assertBuildLinkedToFilter(Branch source, Branch target, String pattern, List<Build> expected) {
        asUserWithView(source, target).call {
            def builds = buildFilterService
                    .standardFilterProviderData(10)
                    .withLinkedTo(pattern)
                    .build()
                    .filterBranchBuilds(source)
            assert expected*.id == builds*.id
        }
    }

    @Test
    void 'Filter on linked from'() {
        // Source branch
        def source = doCreateBranch()
        def b1 = doCreateBuild(source, nd('1.0.0', ''))
        def b2 = doCreateBuild(source, nd('1.1.0', ''))
        // Target branch
        def target = doCreateBranch()
        def t1 = doCreateBuild(target, nd('2.0.0', ''))
        def t2 = doCreateBuild(target, nd('2.1.0', ''))
        // Creates links
        asUser().withView(target).with(source, BuildConfig).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertBuildLinkedFromFilter source, target, source.project.name, [t2, t1]
        // Standard filter on project and all builds
        assertBuildLinkedFromFilter source, target, "${source.project.name}:", [t2, t1]
        // Standard filter on project and all builds, using *
        assertBuildLinkedFromFilter source, target, "${source.project.name}:*", [t2, t1]
        // Standard filter on project and common build prefix
        assertBuildLinkedFromFilter source, target, "${source.project.name}:1*", [t2, t1]
        // Standard filter on project and prefix for one
        assertBuildLinkedFromFilter source, target, "${source.project.name}:1.0*", [t1]
        // Standard filter on project and exact build
        assertBuildLinkedFromFilter source, target, "${source.project.name}:1.0.0", [t1]
    }

    private void assertBuildLinkedFromFilter(Branch source, Branch target, String pattern, List<Build> expected) {
        asUserWithView(source, target).call {
            def builds = buildFilterService
                    .standardFilterProviderData(10)
                    .withLinkedFrom(pattern)
                    .build()
                    .filterBranchBuilds(target)
            assert expected*.id == builds*.id
        }
    }

    @Test
    void 'Project search on build linked from'() {
        // Source branch
        def source = doCreateBranch()
        def b1 = doCreateBuild(source, nd('1.0.0', ''))
        def b2 = doCreateBuild(source, nd('1.1.0', ''))
        // Target branch
        def target = doCreateBranch()
        def t1 = doCreateBuild(target, nd('2.0.0', ''))
        def t2 = doCreateBuild(target, nd('2.1.0', ''))
        // Creates links
        asUser().withView(target).with(source, BuildConfig).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertProjectLinkedFromFilter source, target, source.project.name, [t2, t1]
        // Standard filter on project and all builds
        assertProjectLinkedFromFilter source, target, "${source.project.name}:", [t2, t1]
        // Standard filter on project and all builds, using *
        assertProjectLinkedFromFilter source, target, "${source.project.name}:*", [t2, t1]
        // Standard filter on project and common build prefix
        assertProjectLinkedFromFilter source, target, "${source.project.name}:1*", [t2, t1]
        // Standard filter on project and prefix for one
        assertProjectLinkedFromFilter source, target, "${source.project.name}:1.0*", [t1]
        // Standard filter on project and exact build
        assertProjectLinkedFromFilter source, target, "${source.project.name}:1.0.0", [t1]
    }

    private void assertProjectLinkedFromFilter(Branch source, Branch target, String pattern, List<Build> expected) {
        asUserWithView(source, target).call {
            def builds = structureService.buildSearch(
                    target.project.id,
                    new BuildSearchForm().withLinkedFrom(pattern)
            )
            assert expected*.id == builds*.id
        }
    }

    @Test
    void 'Project search on linked to'() {
        // Source branch
        def source = doCreateBranch()
        def b1 = doCreateBuild(source, nd('1.0.0', ''))
        def b2 = doCreateBuild(source, nd('1.1.0', ''))
        // Target branch
        def target = doCreateBranch()
        def t1 = doCreateBuild(target, nd('2.0.0', ''))
        def t2 = doCreateBuild(target, nd('2.1.0', ''))
        // Creates links
        asUser().withView(target).with(source, BuildConfig).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertProjectLinkedToFilter source, target, target.project.name, [b2, b1]
        // Standard filter on project and all builds
        assertProjectLinkedToFilter source, target, "${target.project.name}:", [b2, b1]
        // Standard filter on project and all builds, using *
        assertProjectLinkedToFilter source, target, "${target.project.name}:*", [b2, b1]
        // Standard filter on project and common build prefix
        assertProjectLinkedToFilter source, target, "${target.project.name}:2*", [b2, b1]
        // Standard filter on project and prefix for one
        assertProjectLinkedToFilter source, target, "${target.project.name}:2.0*", [b1]
        // Standard filter on project and exact build
        assertProjectLinkedToFilter source, target, "${target.project.name}:2.0.0", [b1]
    }

    private void assertProjectLinkedToFilter(Branch source, Branch target, String pattern, List<Build> expected) {
        asUserWithView(source, target).call {
            def builds = structureService.buildSearch(
                    source.project.id,
                    new BuildSearchForm().withLinkedTo(pattern)
            )
            assert expected*.id == builds*.id
        }
    }

}
