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
                val results = searchService.search(SearchRequest("$name:$value")).toList()
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