package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestPropertySource(
        properties = [
            "ontrack.config.search.engine=elasticsearch",
            "ontrack.config.search.index.immediate=true"
        ]
)
class SearchGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Looking for a branch`() {
        project {
            branch {
                val data = run("""{
                    search(type: "branch", token: "$name") {
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
                }""")
                val results = data["search"]
                val result = results.find { it["title"].asText() == entityDisplayName }
                assertNotNull(result, "Branch found") { node ->
                    assertEquals("branch", node["type"]["id"].asText())
                    assertEquals("Branch", node["type"]["name"].asText())
                }
            }
        }
    }

}