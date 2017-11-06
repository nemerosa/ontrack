package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Integration tests around the `builds` root query.
 */
class BuildGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Promotion runs when promotion does not exist`() {
        // Creates a build
        val build = doCreateBuild()
        // Looks for promotion runs
        val data = asUser().withView(build).call {
            run("""
                {
                    builds(id: ${build.id}) {
                        name
                        promotionRuns(promotion: "PLATINUM") {
                          creation {
                            time
                          }
                        }
                    }
                }
            """.trimIndent())
        }
        // Checks the build
        val b = data["builds"][0]
        assertEquals(build.name, b["name"].asText())
        // Checks that there is no promotion run (but the query did not fail!)
        assertEquals(0, b["promotionRuns"].size())
    }

}