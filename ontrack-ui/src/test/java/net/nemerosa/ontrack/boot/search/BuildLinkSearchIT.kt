package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.extension.general.BUILD_LINK_SEARCH_INDEX
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildLinkSearchIT : AbstractSearchTestSupport() {

    @Test
    fun `Looking for linked build base on target project and name`() {
        simpleTest { source, target ->
            // Indexation
            index(BUILD_LINK_SEARCH_INDEX)
            // Looks for the source using target complete reference
            val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
            assertTrue(results.isNotEmpty())
            results[0].apply {
                assertEquals(source.entityDisplayName, title)
                assertEquals("Linked to ${target.project.name}:${target.name}", description)
            }
        }
    }

    @Test
    fun `Looking for linked build base on target project and release property`() {
        // Target build
        val version = uid("V")
        val target = project<Build> {
            branch<Build> {
                build {
                    release(version)
                }
            }
        }
        // Source build
        val source = project<Build> {
            branch<Build> {
                build {
                    linkTo(target)
                }
            }
        }
        // Indexation
        index(BUILD_LINK_SEARCH_INDEX)
        // Looks for the source using target complete reference
        val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:$version", "build-link")).items
        assertTrue(results.isNotEmpty())
        results[0].apply {
            assertEquals(source.entityDisplayName, title)
            assertEquals("Linked to ${target.project.name}:${target.name}", description)
        }
    }

    @Test
    fun `Looking for linked build base on target project only`() {
        simpleTest { source, target ->
            // Indexation
            index(BUILD_LINK_SEARCH_INDEX)
            // Looks for the source using target project only
            val results = searchService.paginatedSearch(SearchRequest(target.project.name, "build-link")).items
            assertTrue(results.isNotEmpty())
            results[0].apply {
                assertEquals(source.entityDisplayName, title)
                assertEquals("Linked to ${target.project.name}:${target.name}", description)
            }
        }
    }

    @Test
    fun `Looking for linked build filters on security access`() {
        // Target build
        val target = project<Build> {
            branch<Build> {
                build()
            }
        }
        // Authorized source build
        val authorizedSource = project<Build> {
            branch<Build> {
                build {
                    linkTo(target)
                }
            }
        }
        // Restricted source build
        val restrictedSource = project<Build> {
            branch<Build> {
                build {
                    linkTo(target)
                }
            }
        }
        // Indexation
        index(BUILD_LINK_SEARCH_INDEX)
        // Looks for the source using target complete reference
        withNoGrantViewToAll {
            asUserWithView(authorizedSource, target) {
                val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
                assertTrue(results.any { it.title == authorizedSource.entityDisplayName })
                assertTrue(results.none { it.title == restrictedSource.entityDisplayName })
            }
            asUserWithView(target) {
                val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
                assertTrue(results.none { it.title == authorizedSource.entityDisplayName })
                assertTrue(results.none { it.title == restrictedSource.entityDisplayName })
            }
            asUser().call {
                val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
                assertTrue(results.none { it.title == authorizedSource.entityDisplayName })
                assertTrue(results.none { it.title == restrictedSource.entityDisplayName })
            }
        }
    }

    @Test
    fun `Looking for a build link after it has been created`() {
        simpleTest { source, target ->
            // Looks for the build based on the link
            assertBuildFoundOnLink(source, target)
            // Deletes the link
            asAdmin {
                source.unlinkTo(target)
            }
            // Looks for the build based on the link
            withNoGrantViewToAll {
                asUserWithView(source, target) {
                    val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
                    assertTrue(results.none { it.title == source.entityDisplayName }, "Build with link shound not be found any longer")
                }
            }
        }
    }

    @Test
    fun `Looking for a build link after its source build has been deleted`() {
        simpleTest { source, target ->
            // Looks for the build based on the link
            assertBuildFoundOnLink(source, target)
            // Deletes the source build
            source.delete()
            // Looks for the build based on the link
            assertBuildNotFoundOnLink(source, target)
        }
    }

    @Test
    fun `Looking for a build link after its target build has been deleted`() {
        simpleTest { source, target ->
            // Looks for the build based on the link
            assertBuildFoundOnLink(source, target)
            // Deletes the target build
            target.delete()
            // Looks for the build based on the link
            assertBuildNotFoundOnLink(source, target)
        }
    }

    @Test
    fun `Looking for a build link after its source branch has been deleted`() {
        simpleTest { source, target ->
            // Looks for the build based on the link
            assertBuildFoundOnLink(source, target)
            // Deletes the source branch has been deleted
            source.branch.delete()
            // Looks for the build based on the link
            assertBuildNotFoundOnLink(source, target)
        }
    }

    @Test
    fun `Looking for a build link after its target branch has been deleted`() {
        simpleTest { source, target ->
            // Looks for the build based on the link
            assertBuildFoundOnLink(source, target)
            // Deletes the source branch has been deleted
            target.branch.delete()
            // Looks for the build based on the link
            assertBuildNotFoundOnLink(source, target)
        }
    }

    private fun assertBuildNotFoundOnLink(source: Build, target: Build) {
        withNoGrantViewToAll {
            asUserWithView(source, target) {
                val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
                assertTrue(results.none { it.title == source.entityDisplayName }, "Build with link shound not be found any longer")
            }
        }
    }

    private fun assertBuildFoundOnLink(source: Build, target: Build) {
        withNoGrantViewToAll {
            asUserWithView(source, target) {
                val results = searchService.paginatedSearch(SearchRequest("${target.project.name}:${target.name}", "build-link")).items
                assertTrue(results.any { it.title == source.entityDisplayName }, "Build with link immediately found")
            }
        }
    }

    private fun simpleTest(code: (source: Build, target: Build) -> Unit) {
        // Target build
        val target = project<Build> {
            branch<Build> {
                build()
            }
        }
        // Source build
        val source = project<Build> {
            branch<Build> {
                build {
                    linkTo(target)
                }
            }
        }
        // Test code
        code(source, target)
    }

}