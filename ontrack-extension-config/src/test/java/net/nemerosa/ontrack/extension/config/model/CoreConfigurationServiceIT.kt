package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.config.EnvFixtures.TEST_BUILD_NUMBER
import net.nemerosa.ontrack.extension.config.EnvFixtures.TEST_VERSION
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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

}