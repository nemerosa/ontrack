package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetaInfoPropertyTypeIT : AbstractPropertyTypeIT() {

    @Autowired
    private lateinit var searchService: SearchService

    @Test
    fun `Search by meta value`() {
        search { _, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("2.1")
            } returns listOf(
                    b2
            )
        }
    }

    @Test
    fun `Search by meta value pattern`() {
        search { b1, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("2.*")
            } returns listOf(
                    b2, b1
            )
        }
    }

    @Test
    fun `Search by meta name`() {
        search { _, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("kotlin:")
            } returns listOf(
                    b2
            )
        }
    }

    @Test
    fun `Search by meta name with pattern`() {
        search { b1, b2 ->
            assertBuildSearch {
                it.withWithProperty(MetaInfoPropertyType::class.java.name)
                        .withWithPropertyValue("java:1.*")
            } returns listOf(
                    b2, b1
            )
        }
    }

    private fun search(test: Branch.(b1: Build, b2: Build) -> Unit) {
        project {
            branch branch@{
                build {}
                build {}
                val b1 = build {
                    metaInfo(
                            "java" to "1.7",
                            "boot" to "1.5",
                            "version" to "2.0"
                    )
                }
                val b2 = build {
                    metaInfo(
                            "java" to "1.8",
                            "kotlin" to "1.2",
                            "boot" to "1.5",
                            "version" to "2.1"
                    )
                }

                this@branch.test(b1, b2)
            }
        }
    }

    @Test
    fun `Since meta info`() {
        project {
            branch branch@{
                build {}
                build {}
                val build1 = build {
                    metaInfo(
                            "java" to "1.7"
                    )
                }
                val build2 = build {}
                val build3 = build {
                    metaInfo(
                            "java" to "1.8"
                    )
                }

                assertBuildSearch {
                    it.withSinceProperty(MetaInfoPropertyType::class.java.name)
                } returns build3

                assertBuildSearch {
                    it.withSinceProperty(MetaInfoPropertyType::class.java.name)
                            .withSincePropertyValue("1.7")
                } returns listOf(build3, build2, build1)
            }
        }
    }

    @Test
    fun `Search meta information with categories`() {
        val prefix = uid("n")
        val category = uid("c")
        val build = project<Build> {
            branch<Build> {
                build {
                    setProperty(
                            this,
                            MetaInfoPropertyType::class.java,
                            MetaInfoProperty(
                                    listOf(
                                            MetaInfoPropertyItem("$prefix-1", "value-1", null, category),
                                            MetaInfoPropertyItem("$prefix-2", "value-2", null, category),
                                            MetaInfoPropertyItem("$prefix-3", "value-3", null, category)
                                    )
                            )
                    )
                }
            }
        }
        // Searching for the build using the search service
        withGrantViewToAll {
            val results = searchService.search(SearchRequest("$prefix-2:value-2"))
            assertEquals(
                    listOf(build.entityDisplayName),
                    results.map {
                        it.title
                    }
            )
        }
    }

    @Test
    fun `Search meta information with categories and no access granted does not return any thing`() {
        val prefix = uid("n")
        val category = uid("c")
        project<Build> {
            branch<Build> {
                build {
                    setProperty(
                            this,
                            MetaInfoPropertyType::class.java,
                            MetaInfoProperty(
                                    listOf(
                                            MetaInfoPropertyItem("$prefix-1", "value-1", null, category),
                                            MetaInfoPropertyItem("$prefix-2", "value-2", null, category),
                                            MetaInfoPropertyItem("$prefix-3", "value-3", null, category)
                                    )
                            )
                    )
                }
            }
        }
        // Searching for the build using the search service
        withNoGrantViewToAll {
            val results = searchService.search(SearchRequest("$prefix-2:value-2"))
            assertTrue(results.isEmpty())
        }
    }

    @Test
    fun `Search meta information with null value`() {
        val prefix = uid("n")
        val category = uid("c")
        val build = project<Build> {
            branch<Build> {
                build {
                    setProperty(
                            this,
                            MetaInfoPropertyType::class.java,
                            MetaInfoProperty(
                                    listOf(
                                            MetaInfoPropertyItem("$prefix-1", null, null, category)
                                    )
                            )
                    )
                }
            }
        }
        // Searching for the build using the search service
        asUserWithView(build) {
            val results = searchService.search(SearchRequest("$prefix-1:"))
            assertTrue(results.isEmpty())
        }
    }

}