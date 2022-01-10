package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.graphql.AbstractQLKTITJUnit4Support
import org.junit.Test
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertTrue

@TestPropertySource(
        properties = [
            "ontrack.config.search.index.immediate=true"
        ]
)
class SearchResultTypesGraphQLIT : AbstractQLKTITJUnit4Support() {

    @Test
    fun `Getting the list of search result types`() {
        val data = run("""{
            searchResultTypes {
                id
                name
                description
                feature { id }
            }
        }""")
        val types = data["searchResultTypes"]
        assertTrue(types.map { it["id"].asText() }.isNotEmpty())
    }

}