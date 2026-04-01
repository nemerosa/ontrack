package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.config.EnvFixtures.TEST_BUILD_NUMBER
import net.nemerosa.ontrack.extension.config.EnvFixtures.TEST_VERSION
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.general.MetaInfoPropertyType
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class CoreConfigurationServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @BeforeEach
    fun init() {
        mockSCMTester.registerRepository("yontrack")
    }

    @Test
    @AsAdminTest
    fun `Configurable build name using a template`() {
        val build = configTestSupport.configureBuild(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    build:
                      buildName: |
                        ${'$'}{env.VERSION}-${'$'}{env.BUILD_NUMBER}-${'$'}{#.datetime?format=yyyyMMdd-HHmmss}
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic(),
        )

        val name = build.name

        val regex = Regex("^${TEST_VERSION}-${TEST_BUILD_NUMBER}-[0-9]{8}-[0-9]{6}$")
        assertTrue(
            name.matches(
                regex
            ),
            "Build name [$name] matches the pattern [$regex]"
        )
    }

    @Test
    @AsAdminTest
    fun `Property configurations are templates`() {
        val build = configTestSupport.configureBuild(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    build:
                      properties:
                        net.nemerosa.ontrack.extension.general.MetaInfoPropertyType:
                          items:
                            - name: appVersion
                              value: ${'$'}{env.APP_VERSION}
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic() + mapOf("APP_VERSION" to "1.0.2"),
        )

        // Getting the meta-info
        val property = propertyService.getPropertyValue(
            build,
            MetaInfoPropertyType::class.java
        )
        assertNotNull(property) {
            assertEquals(
                "1.0.2",
                it.getValue("appVersion")
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Property configurations are templates using alias`() {
        val build = configTestSupport.configureBuild(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    build:
                      properties:
                        metaInfo:
                          - name: appVersion
                            value: ${'$'}{env.APP_VERSION}
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic() + mapOf("APP_VERSION" to "1.0.2"),
        )

        // Getting the meta-info
        val property = propertyService.getPropertyValue(
            build,
            MetaInfoPropertyType::class.java
        )
        assertNotNull(property) {
            assertEquals(
                "1.0.2",
                it.getValue("appVersion")
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Validations and promotions additions in a condition`() {
        val branch = configTestSupport.configureBranch(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    branch:
                      validations:
                        unit-test: {}
                        long-it: {}
                      promotions:
                        BRONZE:
                          validations:
                            - unit-test
                        SILVER:
                          promotions:
                            - BRONZE
                          validations:
                            - long-it
                  custom:
                    configs:
                      - conditions:
                          - name: branch
                            config: main
                        branch:
                          validations:
                            it-pilot: {}
                            it-live: {}
                          promotions:
                            SILVER:
                              validations:
                                - it-pilot
                                - it-live
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic(scmBranch = "main"),
        )

        // Checking the validations

        val vsList = structureService.getValidationStampListForBranch(branch.id)
        assertEquals(
            listOf("unit-test", "long-it", "it-pilot", "it-live").sorted(),
            vsList.map { it.name }.sorted()
        )

        // Checking the BRONZE promotion

        val bronze = structureService.findPromotionLevelByName(branch.project.name, branch.name, "BRONZE")
            .getOrNull()
            ?: fail("Missing BRONZE promotion")

        val bronzeValidations = propertyService.getPropertyValue(bronze, AutoPromotionPropertyType::class.java)
        assertNotNull(bronzeValidations) {
            assertEquals(listOf("unit-test"), it.validationStamps.map { it.name })
            assertEquals(emptyList(), it.promotionLevels)
        }

        // Checking the SILVER promotion

        val silver = structureService.findPromotionLevelByName(branch.project.name, branch.name, "SILVER")
            .getOrNull()
            ?: fail("Missing SILVER promotion")

        val silverValidations = propertyService.getPropertyValue(silver, AutoPromotionPropertyType::class.java)
        assertNotNull(silverValidations) { p ->
            assertEquals(
                listOf("long-it", "it-pilot", "it-live").sorted(),
                p.validationStamps.map { it.name }.sorted()
            )
            assertEquals(listOf("BRONZE"), p.promotionLevels.map { it.name })
        }
    }

}