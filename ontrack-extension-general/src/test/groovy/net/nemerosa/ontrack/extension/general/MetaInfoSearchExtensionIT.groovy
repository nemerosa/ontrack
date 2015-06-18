package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.SearchResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.junit.Assert.assertEquals

class MetaInfoSearchExtensionIT extends AbstractServiceTestSupport {

    @Autowired
    private MetaInfoSearchExtension extension

    @Autowired
    private PropertyService propertyService

    @Test
    void 'Searching on meta property - found one build'() {
        // Creates a build
        def build = doCreateBuild()
        // Meta info on the build
        asUser().with(build, ProjectConfig).call {
            propertyService.editProperty(
                    build,
                    MetaInfoPropertyType,
                    new MetaInfoProperty([
                            new MetaInfoPropertyItem("name", "value", "")
                    ])
            )
        }
        // Searching
        def results = asUser().with(build, ProjectView).call {
            extension.search("name:val*")
        }
        assert results == [
                new SearchResult(
                        build.entityDisplayName,
                        "name -> value",
                        URI.create("urn:test:entity:BUILD:${build.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build.id}"),
                        100
                )
        ]
    }

    @Test
    void 'Searching on meta property - found two builds'() {
        // Context
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        asUser().with(branch, ProjectConfig).call {
            [build1, build2].eachWithIndex { build, index ->
                propertyService.editProperty(
                        build,
                        MetaInfoPropertyType,
                        new MetaInfoProperty([
                                new MetaInfoPropertyItem("name", "value${index + 1}", "")
                        ])
                )
            }
        }
        // Searching
        def results = asUser().with(branch, ProjectView).call {
            extension.search("name:val*")
        }
        assertEquals([
                new SearchResult(
                        build2.entityDisplayName,
                        "name -> value2",
                        URI.create("urn:test:entity:BUILD:${build2.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build2.id}"),
                        100
                ),
                new SearchResult(
                        build1.entityDisplayName,
                        "name -> value1",
                        URI.create("urn:test:entity:BUILD:${build1.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build1.id}"),
                        100
                ),
        ], results)
    }

    @Test
    void 'Searching on meta property - found one build among two ones'() {
        // Context
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        asUser().with(branch, ProjectConfig).call {
            [build1, build2].eachWithIndex { build, index ->
                propertyService.editProperty(
                        build,
                        MetaInfoPropertyType,
                        new MetaInfoProperty([
                                new MetaInfoPropertyItem("name", "value${index + 1}", "")
                        ])
                )
            }
        }
        // Searching
        def results = asUser().with(branch, ProjectView).call {
            extension.search("name:value1*")
        }
        assert results == [
                new SearchResult(
                        build1.entityDisplayName,
                        "name -> value1",
                        URI.create("urn:test:entity:BUILD:${build1.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build1.id}"),
                        100
                ),
        ]
    }

}
