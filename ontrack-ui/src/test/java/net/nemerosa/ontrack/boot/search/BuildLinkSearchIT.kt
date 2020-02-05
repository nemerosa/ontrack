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
        // Indexation
        index(BUILD_LINK_SEARCH_INDEX)
        // Looks for the source using target complete reference
        val results = searchService.search(SearchRequest("${target.project.name}:${target.name}", "build-link")).toList()
        assertTrue(results.isNotEmpty())
        results[0].apply {
            assertEquals(source.entityDisplayName, title)
            assertEquals("Linked to ${target.project.name}:${target.name}", description)
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
        val results = searchService.search(SearchRequest("${target.project.name}:$version", "build-link")).toList()
        assertTrue(results.isNotEmpty())
        results[0].apply {
            assertEquals(source.entityDisplayName, title)
            assertEquals("Linked to ${target.project.name}:${target.name}", description)
        }
    }

    @Test
    fun `Looking for linked build base on target project only`() {
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
        // Indexation
        index(BUILD_LINK_SEARCH_INDEX)
        // Looks for the source using target project only
        val results = searchService.search(SearchRequest(target.project.name, "build-link")).toList()
        assertTrue(results.isNotEmpty())
        results[0].apply {
            assertEquals(source.entityDisplayName, title)
            assertEquals("Linked to ${target.project.name}:${target.name}", description)
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
                val results = searchService.search(SearchRequest("${target.project.name}:${target.name}", "build-link")).toList()
                assertTrue(results.any { it.title == authorizedSource.entityDisplayName })
                assertTrue(results.none { it.title == restrictedSource.entityDisplayName })
            }
            asUserWithView(target) {
                val results = searchService.search(SearchRequest("${target.project.name}:${target.name}", "build-link")).toList()
                assertTrue(results.none { it.title == authorizedSource.entityDisplayName })
                assertTrue(results.none { it.title == restrictedSource.entityDisplayName })
            }
            asUser().call {
                val results = searchService.search(SearchRequest("${target.project.name}:${target.name}", "build-link")).toList()
                assertTrue(results.none { it.title == authorizedSource.entityDisplayName })
                assertTrue(results.none { it.title == restrictedSource.entityDisplayName })
            }
        }
    }

}