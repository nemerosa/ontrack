package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Integration tests around the `promotionLevel` root query.
 */
class PromotionLevelGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Annotated description`() {
        project {
            branch {
                val pl = promotionLevel(description = "A description linking to https://documentation.org/reference")
                val data = run("""{
                    promotionLevel(id: ${pl.id}) {
                        name
                        description
                        annotatedDescription
                    }
                }""")
                val promotion = data["promotionLevel"]
                assertEquals(pl.name, promotion["name"].textValue())
                assertEquals("A description linking to https://documentation.org/reference", promotion["description"].textValue())
                assertEquals("""A description linking to <a href="https://documentation.org/reference" target="_blank">https://documentation.org/reference</a>""", promotion["annotatedDescription"].textValue())
            }
        }
    }

}