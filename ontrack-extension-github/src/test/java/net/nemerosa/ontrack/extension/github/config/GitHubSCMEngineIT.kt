package net.nemerosa.ontrack.extension.github.config

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
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
            configTestSupport.withConfigServiceBuild(
                env = EnvFixtures.gitHub(),
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the project with existing configuration being detected`() {
        val config = gitHubConfiguration()
        val project = configTestSupport.withConfigServiceProject(
            ci = null,
            scm = null,
            env = EnvFixtures.gitHub(),
        )
        assertNotNull(
            propertyService.getPropertyValue(project, GitHubProjectConfigurationPropertyType::class.java),
            "GitHub project configuration has been set"
        ) {
            assertEquals(
                config.name,
                it.configuration.name,
            )
            assertEquals(
                "yontrack/yontrack",
                it.repository,
            )
            assertEquals(
                0,
                it.indexationInterval,
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the project with indexation interval`() {
        val config = gitHubConfiguration()
        val project = configTestSupport.withConfigServiceProject(
            yaml = """
                version: v1
                configuration:
                  defaults:
                    project:
                      scmIndexationInterval: 30
            """.trimIndent(),
            ci = null,
            scm = null,
            env = EnvFixtures.gitHub(),
        )
        assertNotNull(
            propertyService.getPropertyValue(project, GitHubProjectConfigurationPropertyType::class.java),
            "GitHub project configuration has been set"
        ) {
            assertEquals(
                config.name,
                it.configuration.name,
            )
            assertEquals(
                "yontrack/yontrack",
                it.repository,
            )
            assertEquals(
                30,
                it.indexationInterval,
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the project with explicit configuration`() {
        val configName = uid("cfg-")
        val config = gitHubConfiguration(gitConfigurationName = configName)
        val project = configTestSupport.withConfigServiceProject(
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
        )
        assertNotNull(
            propertyService.getPropertyValue(project, GitHubProjectConfigurationPropertyType::class.java),
            "GitHub project configuration has been set"
        ) {
            assertEquals(
                config.name,
                it.configuration.name,
            )
            assertEquals(
                "yontrack/yontrack",
                it.repository,
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the branch with the Git branch`() {
        gitHubConfiguration()
        val branch = configTestSupport.withConfigServiceBranch(
            ci = null,
            scm = null,
            env = EnvFixtures.gitHub(),
        )
        assertNotNull(
            propertyService.getPropertyValue(branch, GitBranchConfigurationPropertyType::class.java),
            "Git branch configuration has been set"
        ) {
            assertEquals(
                EnvFixtures.TEST_BRANCH,
                it.branch,
            )
            assertEquals(
                GitCommitPropertyCommitLink.ID,
                it.buildCommitLink?.id,
            )
            assertEquals(
                false,
                it.override,
            )
            assertEquals(
                0,
                it.buildTagInterval,
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Configuration of the build with the Git commit`() {
        gitHubConfiguration()
        val build = configTestSupport.withConfigServiceBuild(
            ci = null,
            scm = null,
            env = EnvFixtures.gitHub(),
        )
        assertNotNull(
            propertyService.getPropertyValue(build, GitCommitPropertyType::class.java),
            "Git build commit has been set"
        ) {
            assertEquals(
                EnvFixtures.TEST_COMMIT,
                it.commit,
            )
        }
    }

}