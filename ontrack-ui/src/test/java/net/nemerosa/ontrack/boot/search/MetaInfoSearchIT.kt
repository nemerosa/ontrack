package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.extension.general.META_INFO_SEARCH_INDEX
import net.nemerosa.ontrack.extension.general.MetaInfoProperty
import net.nemerosa.ontrack.extension.general.MetaInfoPropertyItem
import net.nemerosa.ontrack.extension.general.MetaInfoPropertyType
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetaInfoSearchIT : AbstractSearchTestSupport() {

    @Test
    fun `Looking for builds with meta information after creation`() {
        val name = uid("N")
        val value = uid("V")
        project {
            branch {
                val build1 = build {
                    metaInfo(name to "${value}1")
                }
                val build2 = build {
                    metaInfo(name to "${value}2")
                }
                // Looks for exact match
                val exactMatches = searchService.paginatedSearch(SearchRequest("$name:${value}1")).items
                val exactMatch = exactMatches.find { it.title == build1.entityDisplayName }
                        ?: error("Exact match not found")
                val variantMatch = exactMatches.find { it.title == build2.entityDisplayName }
                        ?: error("Variant not found")
                assertTrue(exactMatch.accuracy > variantMatch.accuracy, "Exact match scope is higher than non exact match")
                // Looks for prefix
                val prefixMatches = searchService.paginatedSearch(SearchRequest("$name:$value")).items
                assertTrue(prefixMatches.any { it.title == build1.entityDisplayName }, "Build 1 found")
                assertTrue(prefixMatches.any { it.title == build2.entityDisplayName }, "Build 2 found")
            }
        }
    }

    @Test
    fun `Looking for builds with meta information after update of property`() {
        val name1 = uid("N")
        val value1 = uid("V")
        val name2 = uid("M")
        val value2 = uid("W")
        project {
            branch {
                val build = build {
                    metaInfo(name1 to value1)
                }
                // Looks for exact match now
                searchService.paginatedSearch(SearchRequest("$name1:$value1")).items.apply {
                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with current meta information")
                }
                // Changing the meta info
                build.metaInfo(name2 to value2)
                // Looks for exact match now
                searchService.paginatedSearch(SearchRequest("$name2:$value2")).items.apply {
                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with new meta information")
                }
            }
        }
    }

    @Test
    fun `Looking for builds with meta information after deletion of property`() {
        val name = uid("N")
        val value = uid("V")
        project {
            branch {
                val build = build {
                    metaInfo(name to value)
                }
                // Looks for exact match now
                searchService.paginatedSearch(SearchRequest("$name:$value")).items.apply {
                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with meta information")
                }
                // Deleting the meta info
                deleteProperty(build, MetaInfoPropertyType::class.java)
                // Looks for exact match now should not return this build
                searchService.paginatedSearch(SearchRequest("$name:$value")).items.apply {
                    assertTrue(none { it.title == build.entityDisplayName }, "Build not found with meta information")
                }
            }
        }
    }

    @Test
    fun `Looking for builds with meta information after deletion of build`() {
        val name = uid("N")
        val value = uid("V")
        project {
            branch {
                val build = build {
                    metaInfo(name to value)
                }
                // Looks for exact match now
                searchService.paginatedSearch(SearchRequest("$name:$value")).items.apply {
                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with meta information")
                }
                // Deleting the build
                asAdmin {
                    structureService.deleteBuild(build.id)
                }
                // Looks for exact match now should not return this build (and not fail horribly)
                searchService.paginatedSearch(SearchRequest("$name:$value")).items.apply {
                    assertTrue(none { it.title == build.entityDisplayName }, "Build not found with meta information")
                }
            }
        }
    }

    @Test
    fun `Looking for builds with several meta info items`() {
        val name1 = uid("N")
        val name2 = uid("N")
        val value1 = uid("V")
        val value2 = uid("V")
        project {
            branch {
                val build1 = build {
                    metaInfo(name1 to value1, name2 to value2)
                }
                val build2 = build {
                    metaInfo(name1 to value1)
                }
                // Indexation of meta information
                index(META_INFO_SEARCH_INDEX)
                // Search should return those two builds
                val results = searchService.paginatedSearch(SearchRequest("$name1:value1")).items
                assertTrue(results.any { it.title == build1.entityDisplayName }, "Build 1 found")
                assertTrue(results.any { it.title == build2.entityDisplayName }, "Build 2 found")
            }
        }
    }

    @Test
    fun `Looking for a build with meta information`() {
        val name = uid("N")
        val value = uid("V")
        project {
            branch {
                val original = build {
                    metaInfo(name to value)
                }
                // Build with same value prefix
                val prefixed = build {
                    metaInfo(name to uid(value))
                }
                // Build with other value but same name
                val otherValue = build {
                    metaInfo(name to uid("W"))
                }
                // Other builds with other name/values
                repeat(4) {
                    build {
                        metaInfo(uid("N") to uid("W"))
                    }
                }

                // Indexation of meta information
                index(META_INFO_SEARCH_INDEX)

                // Looks for the exact match
                val results = searchService.paginatedSearch(SearchRequest("$name:$value")).items
                assertTrue(results.size > 1)
                // First build is the original one
                results[0].apply {
                    assertEquals(original.entityDisplayName, title)
                    assertEquals("$name -> $value", description)
                    assertEquals(results.map { it.accuracy }.max(), accuracy)
                }
                // Prefixed & other values are also present
                assertTrue(results.any { it.title == prefixed.entityDisplayName }, "Prefixed build is present")
                assertTrue(results.any { it.title == otherValue.entityDisplayName }, "Build with other value but same name is also present")
            }
        }
    }

    private fun Build.metaInfo(vararg items: Pair<String, String>) {
        setProperty(this, MetaInfoPropertyType::class.java, MetaInfoProperty(
                items.map { (name, value) ->
                    MetaInfoPropertyItem(name, value, category = null, link = null)
                }
        ))
    }

}