package net.nemerosa.ontrack.extension.config.graphql

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Disabled("WIP")
class CIConfigurationMutationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var buildDisplayNameService: BuildDisplayNameService

    @Test
    @AsAdminTest
    fun `Default configuration`() {
        val config = """
            configuration: {}
        """.trimIndent()
        run(
            """
                mutation ConfigureBuild(
                    ${'$'}config: String!
                ) {
                    configureBuild(input: {
                        config: ${'$'}config,
                        ci: "generic",
                        scm: "mock",
                        env: [{
                            name: "MOCK_SCM_NAME"
                            value: "yontrack"
                        }, {
                            name: "BRANCH_NAME"
                            value: "release/5.1"
                        }, {
                            name: "BUILD_NUMBER"
                            value: "23"
                        }, {
                            name: "VERSION"
                            value: "5.1.12"
                        }]
                    }) {
                        errors {
                            message
                            exception
                        }
                        build {
                            id
                        }
                    }
                }
            """,
            mapOf("config" to config)
        ) { data ->
            checkGraphQLUserErrors(data, "configureBuild") { payload ->
                val buildId = payload.path("build").path("id").asInt()
                assertNotNull(
                    structureService.findProjectByName("yontrack").getOrNull(),
                    "Project has been created"
                ) { project ->
                    // TODO Project SCM config
                    // Branch
                    assertNotNull(
                        structureService.findBranchByName(project.name, "release-5.1").getOrNull(),
                        "Branch has been created"
                    ) { branch ->
                        // TODO Branch SCM config
                        // Build
                        assertNotNull(
                            structureService.getLastBuild(branch.id).getOrNull(),
                            "Build has been created"
                        ) { build ->
                            // TODO Build SCM config
                            // Build ID
                            assertEquals(buildId, build.id())
                            // Build name
                            assertTrue(
                                build.name.endsWith("-23"),
                                "Build name ends with the build number"
                            )
                            // Build display name
                            assertEquals(
                                "5.1.12",
                                buildDisplayNameService.getFirstBuildDisplayName(build),
                                "Build has the expected version as display name"
                            )
                        }
                    }
                }
            }
        }

    }
}
