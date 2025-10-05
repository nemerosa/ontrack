package net.nemerosa.ontrack.extension.config.graphql

import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationConfig
import net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

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
                            name: "PROJECT_NAME"
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

    @Test
    @AsAdminTest
    fun `Branch validations`() {
        val config = """
            configuration:
              defaults:
                  branch:
                    validations:
                      build:
                        tests: {}
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
                            name: "PROJECT_NAME"
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
                assertNotNull(
                    structureService.findProjectByName("yontrack").getOrNull(),
                    "Project has been created"
                ) { project ->
                    // Branch
                    assertNotNull(
                        structureService.findBranchByName(project.name, "release-5.1").getOrNull(),
                        "Branch has been created"
                    ) { branch ->
                        // Checks the validations
                        val vs = structureService.getValidationStampListForBranch(branch.id)
                            .singleOrNull()
                            ?: fail("One and only one validation stamp is expected")
                        assertEquals("build", vs.name)
                        assertNotNull(vs.dataType) {
                            assertEquals(
                                "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
                                it.descriptor.id
                            )
                            assertEquals(
                                mapOf("warningIfSkipped" to false, "failWhenNoResults" to false).asJson(),
                                it.config.asJson()
                            )
                        }
                    }
                }
            }
        }

    }

    @Test
    @AsAdminTest
    fun `Branch promotions`() {
        val config = """
            configuration:
              defaults:
                  branch:
                    validations:
                      build:
                        tests: {}
                    promotions:
                      BRONZE:
                        validations:
                          - build
                      SILVER:
                        promotions:
                          - BRONZE
                        validations:
                          - scan
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
                            name: "PROJECT_NAME"
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
                assertNotNull(
                    structureService.findProjectByName("yontrack").getOrNull(),
                    "Project has been created"
                ) { project ->
                    // Branch
                    assertNotNull(
                        structureService.findBranchByName(project.name, "release-5.1").getOrNull(),
                        "Branch has been created"
                    ) { branch ->
                        // Checks the promotions
                        val promotions = structureService.getPromotionLevelListForBranch(branch.id)
                        assertEquals(2, promotions.size, "Two promotions created")

                        assertNotNull(
                            promotions.first { it.name == "BRONZE" },
                            "BRONZE promotion has been created"
                        ) { pl ->
                            assertNotNull(
                                propertyService.getPropertyValue(pl, AutoPromotionPropertyType::class.java),
                                "Auto promotion property has been set"
                            ) { p ->
                                assertEquals(
                                    listOf("build"),
                                    p.validationStamps.map { it.name }
                                )
                                assertEquals(0, p.promotionLevels.size)
                            }
                        }

                        assertNotNull(
                            promotions.first { it.name == "SILVER" },
                            "SILVER promotion has been created"
                        ) { pl ->
                            assertNotNull(
                                propertyService.getPropertyValue(pl, AutoPromotionPropertyType::class.java),
                                "Auto promotion property has been set"
                            ) { p ->
                                assertEquals(
                                    listOf("scan"),
                                    p.validationStamps.map { it.name }
                                )
                                assertEquals(
                                    listOf("BRONZE"),
                                    p.promotionLevels.map { it.name }
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    @Test
    @AsAdminTest
    fun `Branch validations with positive condition`() {
        val config = """
            configuration:
              defaults:
                  branch:
                    validations:
                      build:
                        tests: {}
              custom:
                configs:
                  - conditions:
                      branch: release.*
                    branch:
                      validations:
                        deploy-tests:
                          tests:
                            warningIfSkipped: true
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
                            name: "PROJECT_NAME"
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
                assertNotNull(
                    structureService.findProjectByName("yontrack").getOrNull(),
                    "Project has been created"
                ) { project ->
                    // Branch
                    assertNotNull(
                        structureService.findBranchByName(project.name, "release-5.1").getOrNull(),
                        "Branch has been created"
                    ) { branch ->
                        val vss = structureService.getValidationStampListForBranch(branch.id)
                        assertEquals(2, vss.size, "Two validation stamps created")

                        assertNotNull(
                            vss.first { it.name == "build" },
                            "Build validation stamp has been created"
                        ) {
                            assertEquals(
                                TestSummaryValidationDataType::class.qualifiedName,
                                it.dataType?.descriptor?.id
                            )
                            assertEquals(
                                TestSummaryValidationConfig(
                                    warningIfSkipped = false,
                                    failWhenNoResults = false,
                                ),
                                it.dataType?.config
                            )
                        }

                        assertNotNull(
                            vss.first { it.name == "deploy-tests" },
                            "Deployment tests validation stamp has been created"
                        ) {
                            assertEquals(
                                TestSummaryValidationDataType::class.qualifiedName,
                                it.dataType?.descriptor?.id
                            )
                            assertEquals(
                                TestSummaryValidationConfig(
                                    warningIfSkipped = true,
                                    failWhenNoResults = false,
                                ),
                                it.dataType?.config
                            )
                        }
                    }
                }
            }
        }

    }
}
