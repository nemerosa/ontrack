package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchGraphQLIT : AbstractSearchTestSupport() {

    @Test
    fun `Looking for a branch`() {
        project {
            branch {
                val data = run("""{
                    search(type: "branch", token: "$name") {
                        pageItems {
                            title
                            description
                            uri
                            page
                            accuracy
                            type {
                                id 
                                name 
                            }
                        }
                    }
                }""")
                val results = data["search"]["pageItems"]
                val result = results.find { it["title"].asText() == entityDisplayName }
                assertNotNull(result, "Branch found") { node ->
                    assertEquals("branch", node["type"]["id"].asText())
                    assertEquals("Branch", node["type"]["name"].asText())
                }
            }
        }
    }

    @Test
    fun `Pagination on builds`() {
        val prefix = uid("v")
        val project = project {
            branch {
                // Creates N builds
                (1..25).forEach {
                    build {
                        release("$prefix $it")
                    }
                }
            }
        }
        // Looks for the builds
        withNoGrantViewToAll {
            project.asUserWithView {
                val variables = mutableMapOf(
                        "token" to prefix,
                        "offset" to 0,
                        "size" to 10
                )
                val query = """query Search(${'$'}token: String!, ${'$'}offset: Int!, ${'$'}size: Int!) {
                    search(type: "build-release", token: ${'$'}token, offset: ${'$'}offset, size: ${'$'}size) {
                        pageInfo {
                            totalSize
                            currentOffset
                            currentSize
                            previousPage { offset size }
                            nextPage { offset size }
                        }
                        pageItems {
                            title
                        }
                    }
                }"""
                // First 10 builds
                val first = run(query, variables)
                first["search"]["pageInfo"].let {
                    assertEquals(25, it["totalSize"].asInt())
                    assertEquals(0, it["currentOffset"].asInt())
                    assertEquals(10, it["currentSize"].asInt())
                    assertTrue(it["previousPage"].isNull)
                    assertNotNull(it["nextPage"]) { page ->
                        assertEquals(10, page["offset"].asInt())
                        assertEquals(10, page["size"].asInt())
                    }
                }
                // Next 10 builds
                val next = run(query, variables + mapOf("offset" to 10))
                next["search"]["pageInfo"].let {
                    assertEquals(25, it["totalSize"].asInt())
                    assertEquals(10, it["currentOffset"].asInt())
                    assertEquals(10, it["currentSize"].asInt())
                    assertNotNull(it["previousPage"]) { page ->
                        assertEquals(0, page["offset"].asInt())
                        assertEquals(10, page["size"].asInt())
                    }
                    assertNotNull(it["nextPage"]) { page ->
                        assertEquals(20, page["offset"].asInt())
                        assertEquals(5, page["size"].asInt())
                    }
                }
                // Last 5 builds
                val last = run(query, variables + mapOf("offset" to 20))
                last["search"]["pageInfo"].let {
                    assertEquals(25, it["totalSize"].asInt())
                    assertEquals(20, it["currentOffset"].asInt())
                    assertEquals(5, it["currentSize"].asInt())
                    assertNotNull(it["previousPage"]) { page ->
                        assertEquals(10, page["offset"].asInt())
                        assertEquals(10, page["size"].asInt())
                    }
                    assertTrue(it["nextPage"].isNull)
                }
            }
        }
    }

}