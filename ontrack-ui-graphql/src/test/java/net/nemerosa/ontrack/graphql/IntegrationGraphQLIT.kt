package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Testing the integration of GraphQL with transactions and security boundaries.
 */
class IntegrationGraphQLIT : AbstractQLKTITSupport() {

    /**
     * Here, we check that the security context is propagated into the different branches
     * of the GraphQL resolution.
     *
     * In order to make the request, we first make sure we are running in a context where
     * authentication is required even in read only.
     */
    @Test
    fun `Security is propagated`() {
        // Creation of a build
        val build = doCreateBuild()
        // Getting the build by name in an "authentication required" context
        withNoGrantViewToAll {
            // Checks that we cannot access the build
            val o = structureService.findBuildByName(
                    build.project.name,
                    build.branch.name,
                    build.name
            )
            assertFalse(o.isPresent, "Build not found")
            // Runs a query
            val data = asUserWithView(build).call {
                run("""
                    {
                      projects(name: "${build.project.name}") {
                        branches(name: "${build.branch.name}") {
                          builds(count: 1) {
                            name
                          }
                        }
                      }
                    }
                """.trimIndent())
            }
            // Gets the build name
            val buildName = data["projects"][0]["branches"][0]["builds"][0]["name"].asText()
            assertEquals(build.name, buildName)
        }
    }

}