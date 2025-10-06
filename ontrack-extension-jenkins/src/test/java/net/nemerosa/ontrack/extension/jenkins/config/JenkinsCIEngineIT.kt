package net.nemerosa.ontrack.extension.jenkins.config

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class JenkinsCIEngineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Test
    @AsAdminTest
    fun `Jenkins generating the project name from the Git URL`() {
        configTestSupport.withConfigAndProject(
            """
                version: v1
                configuration: {}
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.jenkins(),
            projectName = "ontrack",
        ) { project, _ ->
            assertEquals("ontrack", project.name)
        }
    }

    @Test
    @AsAdminTest
    fun `Jenkins generating the project name from an environment variable`() {
        configTestSupport.withConfigAndProject(
            """
                version: v1
                configuration: {}
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.jenkins(
                extraEnv = mapOf("PROJECT_NAME" to "yontrack"),
            )
        ) { project, _ ->
            assertEquals("yontrack", project.name)
        }
    }

    @Test
    @AsAdminTest
    fun `Jenkins generating the project name from the configuration`() {
        configTestSupport.withConfigAndProject(
            """
                version: v1
                configuration:
                  defaults:
                    project:
                      name: "nemerosa"
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.jenkins(
                extraEnv = mapOf(
                    "PROJECT_NAME" to "yontrack",
                    "GIT_URL" to "https://github.com/nemerosa/ontrack.git",
                )
            ),
            projectName = "nemerosa"
        ) { project, _ ->
            assertEquals("nemerosa", project.name)
        }
    }

}