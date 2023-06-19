package net.nemerosa.ontrack.graphql

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Management of build links using GraphQL.
 */
class BuildLinksGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Linking a build to another build using names`() {
        val build1 = doCreateBuild()
        val build2 = doCreateBuild()
        asAdmin {
            run(
                """
                    mutation {
                        linkBuild(input: {
                            fromProject: "${build1.project.name}",
                            fromBuild: "${build1.name}",
                            toProject: "${build2.project.name}",
                            toBuild: "${build2.name}",
                        }) {
                            build {
                                id
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "linkBuild") { payload ->
                    assertEquals(build1.id(), payload.path("build").path("id").asInt())
                    assertNotNull(
                        structureService.getBuildsUsedBy(build1).pageItems.find { it.id == build2.id },
                        "Link has been created"
                    )
                }
            }
        }
    }

    @Test
    fun `Bulk addition of build links`() {
        val build0 = doCreateBuild()
        val builds = (0..2).map { doCreateBuild() }
        val links = builds.map {
            mapOf(
                "project" to it.project.name,
                "build" to it.name,
            )
        }
        asAdmin {
            run(
                """
                    mutation BulkLinks(${'$'}links: [LinksBuildInputItem!]!) {
                        linksBuild(input: {
                            fromProject: "${build0.project.name}",
                            fromBuild: "${build0.name}",
                            links: ${'$'}links,
                        }) {
                            build {
                                id
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """,
                mapOf("links" to links)
            ) { data ->
                checkGraphQLUserErrors(data, "linksBuild") { payload ->
                    assertEquals(build0.id(), payload.path("build").path("id").asInt())
                    (0..2).forEach { no ->
                        assertNotNull(
                            structureService.getBuildsUsedBy(build0).pageItems.find { it.id == builds[no].id },
                            "Link has been created"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Linking a build to another build using IDs`() {
        val build1 = doCreateBuild()
        val build2 = doCreateBuild()
        asAdmin {
            run(
                """
                    mutation {
                        linkBuildById(input: {
                            fromBuild: ${build1.id},
                            toBuild: ${build2.id},
                        }) {
                            build {
                                id
                            }
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "linkBuildById") { payload ->
                    assertEquals(build1.id(), payload.path("build").path("id").asInt())
                    assertNotNull(
                        structureService.getBuildsUsedBy(build1).pageItems.find { it.id == build2.id },
                        "Link has been created"
                    )
                }
            }
        }
    }

}