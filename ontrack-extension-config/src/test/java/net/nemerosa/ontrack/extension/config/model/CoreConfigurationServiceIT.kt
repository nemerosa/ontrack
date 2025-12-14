package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.config.EnvFixtures.TEST_BUILD_NUMBER
import net.nemerosa.ontrack.extension.config.EnvFixtures.TEST_VERSION
import net.nemerosa.ontrack.extension.general.MetaInfoPropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CoreConfigurationServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

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

}