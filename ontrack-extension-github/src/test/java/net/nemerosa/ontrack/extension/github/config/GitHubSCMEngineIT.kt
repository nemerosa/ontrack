package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GitHubSCMEngineIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var gitHubConfigurationService: GitHubConfigurationService

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @BeforeEach
    fun cleanup() {
        gitHubConfigurationService.configurations.forEach {
            gitHubConfigurationService.deleteConfiguration(it.name)
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the project with unexisting configuration`() {
        assertFailsWith<GitHubSCMUnexistingConfigException> {
            configTestSupport.withConfigAndProject(
                env = EnvFixtures.gitHub(),
            ) { _, _ -> }
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the project with existing configuration being detected`() {
        val config = gitHubConfiguration()
        configTestSupport.withConfigAndProject(
            ci = null,
            scm = null,
            env = EnvFixtures.gitHub(),
        ) { project, _ ->
            assertNotNull(
                propertyService.getPropertyValue(project, GitHubProjectConfigurationPropertyType::class.java),
                "GitHub project configuration has been set"
            ) {
                assertEquals(
                    config.name,
                    it.configuration.name,
                )
                assertEquals(
                    "nemerosa/yontrack",
                    it.repository,
                )
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the project with explicit configuration`() {
        val configName = uid("cfg-")
        val config = gitHubConfiguration(gitConfigurationName = configName)
        configTestSupport.withConfigAndProject(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    project:
                      scmConfig: $configName
            """.trimIndent(),
            ci = null,
            scm = null,
            env = EnvFixtures.gitHub(),
        ) { project, _ ->
            assertNotNull(
                propertyService.getPropertyValue(project, GitHubProjectConfigurationPropertyType::class.java),
                "GitHub project configuration has been set"
            ) {
                assertEquals(
                    config.name,
                    it.configuration.name,
                )
                assertEquals(
                    "nemerosa/yontrack",
                    it.repository,
                )
            }
        }
    }

}