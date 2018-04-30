package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests around the `builds` root query.
 */
class BuildGraphQLIT : AbstractQLKTITSupport() {


    @Test
    fun `Build links are empty by default`() {
        val build = doCreateBuild()

        val data = run("""{
            builds(id: ${build.id}) {
                linkedBuilds {
                    name
                }
            }
        }""")

        val b = data["builds"].first()
        assertNotNull(b["linkedBuilds"]) {
            assertTrue(it.size() == 0)
        }
    }

    @Test
    fun `Build links`() {
        val build = doCreateBuild()
        val targetBuild = doCreateBuild()

        asAdmin().execute {
            structureService.addBuildLink(build, targetBuild)
        }

        val data = run("""{
            builds(id: ${build.id}) {
                linkedBuilds {
                    name
                    branch {
                        name
                        project {
                            name
                        }
                    }
                }
            }
        }""")

        val links = data["builds"].first()["linkedBuilds"]
        assertNotNull(links) {
            assertEquals(1, it.size())
            val link = it.first()
            assertEquals(targetBuild.name, link["name"].asText())
            assertEquals(targetBuild.branch.name, link["branch"]["name"].asText())
            assertEquals(targetBuild.branch.project.name, link["branch"]["project"]["name"].asText())
        }

    }

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

    @Test
    fun `Validations when validation stamp does not exist`() {
        // Creates a build
        val build = doCreateBuild()
        // Looks for validations
        val data = asUser().withView(build).call {
            run("""
                {
                    builds(id: ${build.id}) {
                        name
                        validations(validationStamp: "VS") {
                          validationRuns(count: 1) {
                            creation {
                              time
                            }
                          }
                        }
                    }
                }
            """.trimIndent())
        }
        // Checks the build
        val b = data["builds"][0]
        assertEquals(build.name, b["name"].asText())
        // Checks that there is no validation run (but the query did not fail!)
        assertEquals(0, b["validations"].size())
    }

}