package net.nemerosa.ontrack.graphql

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Testing the integration of GraphQL with transactions and security boundaries.
 */
class IntegrationGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var testDataSource: DataSource

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

    @Test
    fun `Transactions are propagated`() {
        // Home query (adapted for test)
        val gql = """
            {
              projects {
                id
                name
                decorations {
                  ...decorationContent
                }
              }
              projectFavourites: projects(favourites: true) {
                id
                name
                disabled
                decorations {
                  ...decorationContent
                }
                branches {
                  id
                  name
                  type
                  disabled
                  decorations {
                    ...decorationContent
                  }
                  latestPromotions: builds(lastPromotions: true, count: 1) {
                    id
                    name
                    promotionRuns {
                      promotionLevel {
                        id
                        name
                        image
                      }
                    }
                  }
                  latestBuild: builds(count: 1) {
                    id
                    name
                  }
                }
              }
            }

            fragment decorationContent on Decoration {
              decorationType
              error
              data
              feature {
                id
              }
            }
        """.trimIndent()
        // Creation of a build (at least some data)
        val build = doCreateBuild()
        // Gets the number of connections ... before
        val connectionsBefore = getCreationCount()
        // Runs a (BIG) query
        asUserWithView(build).call { run(gql) }
        // Gets the number of connections ... after
        val connectionsAfter = getCreationCount()
        // Checks all is closed
        assertEquals(0, connectionsAfter - connectionsBefore, "Only one connection")
    }

    private fun getCreationCount(): Long {
        val t = testDataSource as org.apache.tomcat.jdbc.pool.DataSource
        val pool = t.pool
        return pool.createdCount
    }

}