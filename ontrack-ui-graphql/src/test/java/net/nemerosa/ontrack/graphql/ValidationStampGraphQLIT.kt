package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Integration tests around the `validationStamp` root query.
 */
class ValidationStampGraphQLIT : AbstractQLKTITSupport() {


    @Test
    fun `No validation stamp`() {
        val data = run("""{
            validationStamp(id: 1) {
                name
            }
        }""")

        val vs = data["validationStamp"]
        assertTrue(vs.isNull, "No validation stamp")
    }


    @Test
    fun `Validation stamp by ID`() {
        val vs = doCreateValidationStamp()
        val data = run("""{
            validationStamp(id: ${vs.id}) {
                name
            }
        }""")

        val name = data["validationStamp"]["name"].asText()
        assertEquals(vs.name, name)
    }

    @Test
    fun `Paginated list of validation runs`() {
        project {
            branch {
                // Creates a validation stamp
                val vs = validationStamp()
                // Creates 3 builds...
                repeat(3) { no ->
                    build("1.$no") {
                        // Validates N times the build
                        repeat(20) {
                            validate(vs)
                        }
                    }
                }
                // Checks the number of validation runs
                assertEquals(
                        60,
                        structureService.getValidationRunsCountForValidationStamp(vs.id),
                        "Checking the number of validation runs having been created"
                )
                // Paginated query with variables
                val query = """
                    query PaginatedValidationRuns(
                        ${'$'}validationStampId: Int!,
                        ${'$'}offset: Int = 0,
                        ${'$'}size: Int = 20) {
                        validationStamp(id: ${'$'}validationStampId) {
                            name
                            validationRunsPaginated(offset: ${'$'}offset, size: ${'$'}size) {
                                pageInfo {
                                    totalSize
                                    currentOffset
                                    currentSize
                                    previousPage {
                                        offset
                                        size
                                    }
                                    nextPage {
                                        offset
                                        size
                                    }
                                    pageIndex
                                    pageTotal
                                }
                                pageItems {
                                    id
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                    """
                // Initial parameters
                val params = mutableMapOf(
                        "offset" to 0,
                        "size" to 20,
                        "validationStampId" to vs.id()
                )

                /**
                 * First page
                 */

                // Runs the query for the first page
                var data = run(query, params)
                // Checks the validation stamp info
                var vsNode = data["validationStamp"]
                assertEquals(vs.name, vsNode["name"].asText())
                // Gets the paginated list
                var paginated = vsNode["validationRunsPaginated"]
                var pageInfo = paginated["pageInfo"]
                var previousPage = pageInfo["previousPage"]
                var nextPage = pageInfo["nextPage"]
                assertEquals(60, pageInfo["totalSize"].asInt())
                assertEquals(0, pageInfo["currentOffset"].asInt())
                assertEquals(20, pageInfo["currentSize"].asInt())
                assertEquals(0, pageInfo["pageIndex"].asInt())
                assertEquals(3, pageInfo["pageTotal"].asInt())
                assertTrue(previousPage.isNull, "No previous page")
                assertFalse(nextPage.isNull, "There is a next page")
                assertEquals(20, nextPage["offset"].asInt())
                assertEquals(20, nextPage["size"].asInt())
                var pageItems = paginated["pageItems"]
                assertEquals(20, pageItems.size())
                repeat(20) {
                    val buildName = pageItems[it]["build"]["name"].asText()
                    assertEquals("1.2", buildName)
                }

                /**
                 * Middle page
                 */

                // Uses the next page as variables
                params["offset"] = nextPage["offset"].asInt()
                params["size"] = nextPage["size"].asInt()
                // Runs the query for the first page
                data = run(query, params)
                // Checks the validation stamp info
                vsNode = data["validationStamp"]
                assertEquals(vs.name, vsNode["name"].asText())
                // Gets the paginated list
                paginated = vsNode["validationRunsPaginated"]
                pageInfo = paginated["pageInfo"]
                previousPage = pageInfo["previousPage"]
                nextPage = pageInfo["nextPage"]
                assertEquals(60, pageInfo["totalSize"].asInt())
                assertEquals(20, pageInfo["currentOffset"].asInt())
                assertEquals(20, pageInfo["currentSize"].asInt())
                assertEquals(1, pageInfo["pageIndex"].asInt())
                assertEquals(3, pageInfo["pageTotal"].asInt())
                assertFalse(previousPage.isNull, "There is a previous page")
                assertEquals(0, previousPage["offset"].asInt())
                assertEquals(20, previousPage["size"].asInt())
                assertFalse(nextPage.isNull, "There is a next page")
                assertEquals(40, nextPage["offset"].asInt())
                assertEquals(20, nextPage["size"].asInt())
                pageItems = paginated["pageItems"]
                assertEquals(20, pageItems.size())
                repeat(20) {
                    val buildName = pageItems[it]["build"]["name"].asText()
                    assertEquals("1.1", buildName)
                }

                /**
                 * Last page
                 */

                // Uses the next page as variables
                params["offset"] = nextPage["offset"].asInt()
                params["size"] = nextPage["size"].asInt()
                // Runs the query for the first page
                data = run(query, params)
                // Checks the validation stamp info
                vsNode = data["validationStamp"]
                assertEquals(vs.name, vsNode["name"].asText())
                // Gets the paginated list
                paginated = vsNode["validationRunsPaginated"]
                pageInfo = paginated["pageInfo"]
                previousPage = pageInfo["previousPage"]
                nextPage = pageInfo["nextPage"]
                assertEquals(60, pageInfo["totalSize"].asInt())
                assertEquals(40, pageInfo["currentOffset"].asInt())
                assertEquals(20, pageInfo["currentSize"].asInt())
                assertEquals(2, pageInfo["pageIndex"].asInt())
                assertEquals(3, pageInfo["pageTotal"].asInt())
                assertFalse(previousPage.isNull, "There is a previous page")
                assertEquals(20, previousPage["offset"].asInt())
                assertEquals(20, previousPage["size"].asInt())
                assertTrue(nextPage.isNull, "There is no next page")
                pageItems = paginated["pageItems"]
                assertEquals(20, pageItems.size())
                repeat(20) {
                    val buildName = pageItems[it]["build"]["name"].asText()
                    assertEquals("1.0", buildName)
                }
            }
        }
    }

}