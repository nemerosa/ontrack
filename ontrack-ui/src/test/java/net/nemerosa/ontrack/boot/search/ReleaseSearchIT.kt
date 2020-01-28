package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.extension.general.RELEASE_SEARCH_INDEX
import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReleaseSearchIT : AbstractSearchTestSupport() {

    @Test
    fun `Searching builds based on release property`() {
        val version = uid("V")
        project {
            branch {
                build {
                    setProperty(this, ReleasePropertyType::class.java, ReleaseProperty(version))
                    // Indexation
                    index(RELEASE_SEARCH_INDEX)
                    // Search
                    val results = searchService.search(SearchRequest(version)).toList()
                    assertTrue(results.isNotEmpty())
                    results[0].apply {
                        assertEquals(entityDisplayName, title)
                        assertEquals("$entityDisplayName having version/label/release $version", description)
                    }
                }
            }
        }
    }

}