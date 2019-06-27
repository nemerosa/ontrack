package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ReleaseSearchExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var searchService: SearchService

    @Test
    fun `Search on label`() {
        val uniqueLabel = uid("V")
        project {
            branch {
                val build = build {
                    setProperty(
                            this,
                            ReleasePropertyType::class.java,
                            ReleaseProperty(uniqueLabel)
                    )
                }
                // Performing a search
                val results: Collection<SearchResult> = searchService.search(
                        SearchRequest(uniqueLabel)
                )
                // One result only
                assertEquals(1, results.size)
                val result = results.first()
                assertEquals(
                        "",
                        result.uri.toString()
                )
            }
        }
    }

}