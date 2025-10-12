package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitHubCIEngineIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Test
    @AsAdminTest
    fun `GitHub CI engine generating the project name from the Git URL`() {
        configTestSupport.withConfigAndProject(
            """
                version: v1
                configuration: {}
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.gitHub(
                projectName = "ontrack",
            ),
            expectedProjectName = "ontrack",
        ) { project, _ ->
            assertEquals("ontrack", project.name)
        }
    }

    @Test
    @AsAdminTest
    fun `GitHub CI engine generating the project name from an environment variable`() {
        configTestSupport.withConfigAndProject(
            """
                version: v1
                configuration: {}
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.gitHub(
                extraEnv = mapOf("PROJECT_NAME" to "yontrack"),
            ),
            expectedProjectName = "yontrack",
        ) { project, _ ->
            assertEquals("yontrack", project.name)
        }
    }

    @Test
    @AsAdminTest
    fun `GitHub CI engine generating the project name from the configuration`() {
        configTestSupport.withConfigAndProject(
            """
                version: v1
                configuration:
                  defaults:
                    project:
                      name: "nemerosa"
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.gitHub(
                extraEnv = mapOf(
                    "PROJECT_NAME" to "yontrack",
                )
            ),
            expectedProjectName = "nemerosa"
        ) { project, _ ->
            assertEquals("nemerosa", project.name)
        }
    }

    @Test
    @AsAdminTest
    fun `GitHub CI engine getting the branch name from the native environment`() {
        configTestSupport.withConfigAndBranch(
            """
                version: v1
                configuration: {}
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.gitHub(),
            expectedProjectName = "yontrack"
        ) { branch, _ ->
            assertEquals("release-5.1", branch.name)
        }
    }

    @Test
    @AsAdminTest
    fun `GitHub CI engine getting the build suffix from the native environment`() {
        configTestSupport.withConfigAndBuild(
            """
                version: v1
                configuration: {}
            """.trimIndent(),
            ci = null,
            env = EnvFixtures.gitHub(),
            expectedProjectName = "yontrack"
        ) { build, _ ->
            assertTrue(build.name.endsWith("-96"), "Build name contains the run number: ${build.name}")
        }
    }

}