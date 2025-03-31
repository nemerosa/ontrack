package net.nemerosa.ontrack.boot.search

//import net.nemerosa.ontrack.extension.general.RELEASE_SEARCH_INDEX
//import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import org.junit.jupiter.api.Disabled

@Disabled("Missing General extension")
class ReleaseSearchIT : AbstractSearchTestSupport() {

//    @Test
//    @Ignore("flaky")
//    fun `Looking for builds with release information after creation`() {
//        val value = uid("V")
//        project {
//            branch {
//                val build1 = build {
//                    release("${value}-1")
//                }
//                val build2 = build {
//                    release("${value}-2")
//                }
//                // Looks for exact match
//                val exactMatches = searchService.paginatedSearch(SearchRequest("${value}-1")).items
//                val exactMatch = exactMatches.find { it.title == build1.entityDisplayName }
//                        ?: error("Exact match not found")
//                val variantMatch = exactMatches.find { it.title == build2.entityDisplayName }
//                        ?: error("Variant not found")
//                assertTrue(exactMatch.accuracy > variantMatch.accuracy, "Exact match scope is higher than non exact match")
//                // Looks for prefix
//                val prefixMatches = searchService.paginatedSearch(SearchRequest(value)).items
//                assertTrue(prefixMatches.any { it.title == build1.entityDisplayName }, "Build 1 found")
//                assertTrue(prefixMatches.any { it.title == build2.entityDisplayName }, "Build 2 found")
//            }
//        }
//    }
//
//    @Test
//    fun `Looking for builds with release information after update of property`() {
//        val value1 = uid("V")
//        val value2 = uid("W")
//        project {
//            branch {
//                val build = build {
//                    release(value1)
//                }
//                // Looks for exact match now
//                searchService.paginatedSearch(SearchRequest(value1)).items.apply {
//                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with current release information")
//                }
//                // Changing the release info
//                build.release(value2)
//                // Looks for exact match now
//                searchService.paginatedSearch(SearchRequest(value2)).items.apply {
//                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with new release information")
//                }
//            }
//        }
//    }
//
//    @Test
//    fun `Looking for builds with release information after deletion of property`() {
//        val value = uid("V")
//        project {
//            branch {
//                val build = build {
//                    release(value)
//                }
//                // Looks for exact match now
//                searchService.paginatedSearch(SearchRequest(value)).items.apply {
//                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with current release information")
//                }
//                // Deleting the release info
//                deleteProperty(build, ReleasePropertyType::class.java)
//                // Looks for exact match now
//                searchService.paginatedSearch(SearchRequest(value)).items.apply {
//                    assertTrue(none { it.title == build.entityDisplayName }, "Build not found with removed release information")
//                }
//            }
//        }
//    }
//
//    @Test
//    fun `Looking for builds with release information after deletion of build`() {
//        val value = uid("V")
//        project {
//            branch {
//                val build = build {
//                    release(value)
//                }
//                // Looks for exact match now
//                searchService.paginatedSearch(SearchRequest(value)).items.apply {
//                    assertTrue(any { it.title == build.entityDisplayName }, "Build found with release information")
//                }
//                // Deleting the build
//                asAdmin {
//                    structureService.deleteBuild(build.id)
//                }
//                // Looks for exact match now should not return this build (and not fail horribly)
//                searchService.paginatedSearch(SearchRequest(value)).items.apply {
//                    assertTrue(none { it.title == build.entityDisplayName }, "Build not found with release information")
//                }
//            }
//        }
//    }
//
//    @Test
//    fun `Searching builds based on release property`() {
//        val version = uid("V")
//        project {
//            branch {
//                build {
//                    release(version)
//                    // Indexation
//                    index(RELEASE_SEARCH_INDEX)
//                    // Search
//                    val results = searchService.paginatedSearch(SearchRequest(version)).items
//                    assertTrue(results.isNotEmpty())
//                    results[0].apply {
//                        assertEquals(entityDisplayName, title)
//                        assertEquals("$entityDisplayName having version/label/release $version", description)
//                    }
//                }
//            }
//        }
//    }

}