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
    fun `Following build links TO`() {
        // Three builds
        val a = doCreateBuild()
        val b = doCreateBuild()
        val c = doCreateBuild()
        // Links: a -> b -> c
        asAdmin().execute {
            structureService.addBuildLink(a, b)
            structureService.addBuildLink(b, c)
        }
        // Query build links of "b" TO
        val data = run("""{
            builds(id: ${b.id}) {
                linkedBuilds(direction: TO) {
                    id
                    name
                }
            }
        }""")
        // Checks the result
        val links = data["builds"].first()["linkedBuilds"]
        assertNotNull(links) {
            assertEquals(1, it.size())
            val link = it.first()
            assertEquals(c.name, link["name"].asText())
            assertEquals(c.id(), link["id"].asInt())
        }
    }

    @Test
    fun `Following build links FROM`() {
        // Three builds
        val a = doCreateBuild()
        val b = doCreateBuild()
        val c = doCreateBuild()
        // Links: a -> b -> c
        asAdmin().execute {
            structureService.addBuildLink(a, b)
            structureService.addBuildLink(b, c)
        }
        // Query build links of "b" FROM
        val data = run("""{
            builds(id: ${b.id}) {
                linkedBuilds(direction: FROM) {
                    id
                    name
                }
            }
        }""")
        // Checks the result
        val links = data["builds"].first()["linkedBuilds"]
        assertNotNull(links) {
            assertEquals(1, it.size())
            val link = it.first()
            assertEquals(a.name, link["name"].asText())
            assertEquals(a.id(), link["id"].asInt())
        }
    }

    @Test
    fun `Following build links BOTH`() {
        // Three builds
        val a = doCreateBuild()
        val b = doCreateBuild()
        val c = doCreateBuild()
        // Links: a -> b -> c
        asAdmin().execute {
            structureService.addBuildLink(a, b)
            structureService.addBuildLink(b, c)
        }
        // Query build links of "b" BOTH
        val data = run("""{
            builds(id: ${b.id}) {
                linkedBuilds(direction: BOTH) {
                    id
                    name
                }
            }
        }""")
        // Checks the result
        val links = data["builds"].first()["linkedBuilds"]
        assertNotNull(links) {
            assertEquals(2, it.size())
            val cLink = it[0]
            assertEquals(c.name, cLink["name"].asText())
            assertEquals(c.id(), cLink["id"].asInt())
            val aLink = it[1]
            assertEquals(a.name, aLink["name"].asText())
            assertEquals(a.id(), aLink["id"].asInt())
        }
    }

    @Test(expected = AssertionError::class)
    fun `Following build links with unknown direction`() {
        val b = doCreateBuild()
        run("""{
            builds(id: ${b.id}) {
                linkedBuilds(direction: TBD) {
                    id
                    name
                }
            }
        }""")
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