package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ReleaseSearchExtensionIT : AbstractGeneralExtensionTestSupport() {

    @Autowired
    private lateinit var searchService: SearchService

    @Test
    fun `Search should return only one result`() {
        val uniqueLabel = uid("V")
        project {
            // Creates several builds with similar release property
            val targetBuild = branch<Build> {
                build {
                    releaseProperty = uniqueLabel
                }
            }
            (1..5).forEach {
                branch {
                    build {
                        releaseProperty = "$uniqueLabel-$it"
                    }
                }
            }
            // Performing a search on exact name
            val results: Collection<SearchResult> = searchService.search(
                    SearchRequest(uniqueLabel)
            )
            // One result only
            assertEquals(1, results.size)
            val result = results.first()
            assertEquals(
                    "Build ${targetBuild.entityDisplayName} having version/label/release $uniqueLabel",
                    result.description
            )
            assertEquals(
                    "urn:test:entity:BUILD:${targetBuild.id}",
                    result.uri.toString()
            )
            assertEquals(
                    "urn:test:#:entity:BUILD:${targetBuild.id}",
                    result.page.toString()
            )
        }
    }

    @Test
    fun `Fuzzy search is also possible`() {
        val uniqueLabel = uid("V")
        project {
            // Creates several builds with similar release property
            val targetBuild = branch<Build> {
                build {
                    releaseProperty = uniqueLabel
                }
            }
            (1..5).forEach {
                branch {
                    build {
                        releaseProperty = "$uniqueLabel-$it"
                    }
                }
            }
            // Performing a search on general release name
            val results: Collection<SearchResult> = searchService.search(
                    SearchRequest("$uniqueLabel*")
            )
            // All results
            assertEquals(6, results.size)
        }
    }

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
                        "Build ${build.entityDisplayName} having version/label/release $uniqueLabel",
                        result.description
                )
                assertEquals(
                        "urn:test:entity:BUILD:${build.id}",
                        result.uri.toString()
                )
                assertEquals(
                        "urn:test:#:entity:BUILD:${build.id}",
                        result.page.toString()
                )
            }
        }
    }

}