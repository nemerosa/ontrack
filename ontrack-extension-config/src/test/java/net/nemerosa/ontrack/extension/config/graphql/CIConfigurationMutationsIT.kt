package net.nemerosa.ontrack.extension.config.graphql

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationData
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationConfig
import net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMBranchPropertyType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMBuildCommitPropertyType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMProjectPropertyType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.assertIs
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

    @Autowired
    private lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Test
    @AsAdminTest
    fun `Default configuration`() {
        val config = """
            version: v1
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
                            name: "BUILD_REVISION"
                            value: "abcd123"
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
                    // Project SCM config
                    assertNotNull(
                        propertyService.getPropertyValue(project, MockSCMProjectPropertyType::class.java),
                        "Project SCM config has been set"
                    ) {
                        assertEquals("yontrack", it.name)
                    }
                    // Branch
                    assertNotNull(
                        structureService.findBranchByName(project.name, "release-5.1").getOrNull(),
                        "Branch has been created"
                    ) { branch ->
                        // Branch SCM config
                        assertNotNull(
                            propertyService.getPropertyValue(branch, MockSCMBranchPropertyType::class.java),
                            "Branch SCM config has been set"
                        ) {
                            assertEquals("release/5.1", it.name)
                        }
                        // Build
                        assertNotNull(
                            structureService.getLastBuild(branch.id).getOrNull(),
                            "Build has been created"
                        ) { build ->
                            // Build SCM config
                            assertNotNull(
                                propertyService.getPropertyValue(build, MockSCMBuildCommitPropertyType::class.java),
                                "Build SCM config has been set"
                            ) {
                                assertEquals("abcd123", it.id)
                            }
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
            version: v1
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
            checkGraphQLUserErrors(data, "configureBuild") { _ ->
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
            version: v1
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
            checkGraphQLUserErrors(data, "configureBuild") { _ ->
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
            version: v1
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
            checkGraphQLUserErrors(data, "configureBuild") { _ ->
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

    @Test
    @AsAdminTest
    fun `Project issue service identifier`() {
        val config = """
            version: v1
            configuration:
              defaults:
                project:
                  issueServiceIdentifier:
                    serviceId: jira
                    serviceName: JIRA
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
            checkGraphQLUserErrors(data, "configureBuild") { _ ->
                assertNotNull(
                    structureService.findProjectByName("yontrack").getOrNull(),
                    "Project has been created"
                ) { project ->
                    assertNotNull(
                        propertyService.getPropertyValue(project, MockSCMProjectPropertyType::class.java),
                        "Project SCM config has been set"
                    ) { property ->
                        assertEquals(
                            "jira//JIRA",
                            property.issueServiceIdentifier
                        )
                    }
                }
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Auto-versioning setup`() {
        withConfigAndBranch(
            """
                version: v1
                configuration:
                  defaults:
                    branch:
                      autoVersioning:
                        configurations:
                          - sourceProject: my-project
                            sourceBranch: main
                            sourcePromotion: GOLD
                            targetPath: versions.properties
                            targetProperty: yontrackVersion
                            validationStamp: my-chart-validator
            """.trimIndent()
        ) { branch ->
            assertNotNull(
                autoVersioningConfigurationService.getAutoVersioning(branch),
                "Auto-versioning config has been set"
            ) { av ->
                val avConfig = av.configurations.single()
                avConfig.apply {
                    assertEquals("my-project", sourceProject)
                    assertEquals("main", sourceBranch)
                    assertEquals("GOLD", sourcePromotion)
                    assertEquals("versions.properties", targetPath)
                    assertEquals("yontrackVersion", targetProperty)
                    assertEquals("my-chart-validator", validationStamp)
                }
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Auto-versioning check`() {
        project {
            val dependency = this
            branch("main") {
                val pl = promotionLevel("GOLD")
                build("5.0.0") {
                    promote(pl)
                }
            }
            mockSCMTester.withMockSCMRepository(name = "yontrack") {

                repositoryFile(
                    branch = "main",
                    path = "versions.properties",
                    content = """
                        yontrackVersion = 5.0.0
                    """.trimIndent()
                )

                withConfigAndBuild(
                    """
                        version: v1
                        configuration:
                          defaults:
                            branch:
                              autoVersioning:
                                configurations:
                                  - sourceProject: ${dependency.name}
                                    sourceBranch: main
                                    sourcePromotion: GOLD
                                    targetPath: versions.properties
                                    targetProperty: yontrackVersion
                                    validationStamp: av-check
                            build:
                              autoVersioningCheck: true
                    """.trimIndent(),
                    scmBranch = "main", // Must match the branch where the versions.properties file is created
                ) { build ->

                    val vs = structureService.findValidationStampByName(
                        project = build.project.name,
                        branch = build.branch.name,
                        validationStamp = "av-check"
                    ).getOrNull() ?: fail("Validation stamp has not been created")

                    val run = structureService.getValidationRunsForBuildAndValidationStamp(
                        build, vs, offset = 0, count = 1
                    ).single()

                    assertNotNull(run.data) { data ->
                        assertIs<AutoVersioningValidationData>(data.data) { avData ->
                            assertEquals(dependency.name, avData.project)
                            assertEquals("5.0.0", avData.version, "Current version OK")
                            assertEquals("5.0.0", avData.latestVersion)
                            assertEquals("versions.properties", avData.path)
                        }
                    }

                }
            }
        }
    }

    private fun withConfigAndProject(
        yaml: String,
        scmBranch: String = "release/5.1",
        code: (project: Project) -> Unit,
    ) =
        withConfig(yaml, scmBranch = scmBranch) {
            assertNotNull(
                structureService.findProjectByName("yontrack").getOrNull(),
                "Project has been created"
            ) { project ->
                code(project)
            }
        }

    private fun withConfigAndBranch(
        yaml: String,
        scmBranch: String = "release/5.1",
        code: (branch: Branch) -> Unit,
    ) =
        withConfigAndProject(yaml, scmBranch = scmBranch) { project ->
            // Branch
            assertNotNull(
                structureService.findBranchByName(project.name, scmBranch).getOrNull(),
                "Branch has been created"
            ) { branch ->
                code(branch)
            }
        }

    private fun withConfigAndBuild(
        yaml: String,
        scmBranch: String = "release/5.1",
        code: (build: Build) -> Unit,
    ) =
        withConfigAndBranch(yaml, scmBranch = scmBranch) { branch ->
            assertNotNull(
                structureService.getLastBuild(branch.id).getOrNull(),
                "Build has been created"
            ) { build ->
                code(build)
            }
        }

    private fun withConfig(
        yaml: String,
        scmBranch: String = "release/5.1",
        env: Map<String, String> = mapOf(
            "PROJECT_NAME" to "yontrack",
            "BRANCH_NAME" to scmBranch,
            "BUILD_NUMBER" to "23",
            "VERSION" to "5.1.12",
        ),
        code: () -> Unit,
    ) {
        run(
            """
                mutation ConfigureBuild(
                    ${'$'}config: String!,
                    ${'$'}env: [CIEnv!]!,
                ) {
                    configureBuild(input: {
                        config: ${'$'}config,
                        ci: "generic",
                        scm: "mock",
                        env: ${'$'}env,
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
            mapOf(
                "config" to yaml,
                "env" to env.map { mapOf("name" to it.key, "value" to it.value) },
            )
        ) { data ->
            checkGraphQLUserErrors(data, "configureBuild") { _ ->
                code()
            }
        }
    }
}
