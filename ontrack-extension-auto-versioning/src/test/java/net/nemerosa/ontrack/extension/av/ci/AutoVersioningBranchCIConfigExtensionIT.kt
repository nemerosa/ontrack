package net.nemerosa.ontrack.extension.av.ci

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationData
import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class AutoVersioningBranchCIConfigExtensionIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Autowired
    private lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Test
    @AsAdminTest
    fun `Auto-versioning setup`() {
        val branch = configTestSupport.configureBranch(
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
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic()
        )
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

                val build = configTestSupport.configureBuild(
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
                    ci = "generic",
                    scm = "mock",
                    env = EnvFixtures.generic()
                )

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
