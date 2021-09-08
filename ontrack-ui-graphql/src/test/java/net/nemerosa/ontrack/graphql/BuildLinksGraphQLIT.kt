package net.nemerosa.ontrack.graphql

import org.junit.Test
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
                            fromBranch: "${build1.branch.name}",
                            fromBuild: "${build1.name}",
                            toProject: "${build2.project.name}",
                            toBranch: "${build2.branch.name}",
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