package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.BuildEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.SearchResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.junit.Assert.assertEquals

class BuildLinkSearchExtensionIT extends AbstractServiceTestSupport {

    @Autowired
    private BuildLinkSearchExtension extension

    @Autowired
    private PropertyService propertyService

    @Test
    void 'Searching on build link property - found one build'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        def targetPrefix = target.name[0..5]
        // Build link on the build
        asUser().with(build, BuildEdit).call {
            propertyService.editProperty(
                    build,
                    BuildLinkPropertyType,
                    new BuildLinkProperty([
                            BuildLinkPropertyItem.of(target)
                    ])
            )
        }
        // Searching
        def results = asUser().with(build, ProjectView).call {
            extension.search("${target.project.name}:${targetPrefix}*")
        }
        assertEquals(
                [
                        new SearchResult(
                                build.entityDisplayName,
                                "${target.project.name} -> ${target.name}",
                                URI.create("urn:test:entity:BUILD:${build.id}"),
                                URI.create("urn:test:#:entity:BUILD:${build.id}"),
                                100
                        )
                ],
                results
        )
    }

    @Test
    void 'Searching on build link - found two builds'() {
        // Creates a target build
        def target = doCreateBuild()
        def targetPrefix = target.name[0..5]
        // Two builds to find
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1.1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("1.2", "Build 2"))
        // Meta info on the build
        [build1, build2].each { build ->
            asUser().with(build, BuildEdit).call {
                propertyService.editProperty(
                        build,
                        BuildLinkPropertyType,
                        new BuildLinkProperty([
                                BuildLinkPropertyItem.of(target),
                        ])
                )
            }
        }
        // Searching
        def results = asUser().with(target, ProjectView).with(branch, ProjectView).call {
            extension.search("${target.project.name}:${targetPrefix}*")
        }
        assertEquals([
                new SearchResult(
                        build2.entityDisplayName,
                        "${target.project.name} -> ${target.name}",
                        URI.create("urn:test:entity:BUILD:${build2.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build2.id}"),
                        100
                ),
                new SearchResult(
                        build1.entityDisplayName,
                        "${target.project.name} -> ${target.name}",
                        URI.create("urn:test:entity:BUILD:${build1.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build1.id}"),
                        100
                ),
        ], results)
    }

    @Test
    void 'Searching on build link property - found one build among two ones'() {
        // Creates two target builds
        def targetBranch = doCreateBranch()
        doCreateBuild(targetBranch, nd('1.0', ''))
        doCreateBuild(targetBranch, nd('2.0', ''))
        // Context
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        asUser().with(branch, BuildEdit).call {
            [build1, build2].eachWithIndex { build, index ->
                propertyService.editProperty(
                        build,
                        BuildLinkPropertyType,
                        new BuildLinkProperty([
                                BuildLinkPropertyItem.of(targetBranch.project.name, "${build.name}.0")
                        ])
                )
            }
        }
        // Searching
        def results = asUser().with(branch, ProjectView).call {
            extension.search("${targetBranch.project.name}:1*")
        }
        assert results == [
                new SearchResult(
                        build1.entityDisplayName,
                        "${targetBranch.project.name} -> 1.0",
                        URI.create("urn:test:entity:BUILD:${build1.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build1.id}"),
                        100
                ),
        ]
    }

}
