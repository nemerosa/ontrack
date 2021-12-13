package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultConfigLoaderServiceTest {

    @Test
    fun `Project not configured returns null`() {
        test(
            projectConfigured = false,
            expectedConfig = false,
        )
    }

    @Test
    fun `Branch not configured returns null`() {
        test(
            branchConfigured = false,
            expectedConfig = false,
        )
    }

    @Test
    fun `Path not found returns null`() {
        test(
            pathFound = false,
            expectedConfig = false,
        )
    }

    @Test
    fun `Loading the configuration`() {
        test()
    }

    @Test
    fun `Configuration parsing error returns null`() {
        test(
            incorrectConfig = true,
            expectedConfig = false,
        )
    }

    private fun test(
        projectConfigured: Boolean = true,
        branchConfigured: Boolean = true,
        pathFound: Boolean = true,
        incorrectConfig: Boolean = false,
        expectedConfig: Boolean = true,
    ) {
        val project = Project.of(NameDescription.nd("test", ""))
        val branch = Branch.of(project, NameDescription.nd("main", ""))

        val gitService = mockk<GitService>()
        every { gitService.getBranchAsPullRequest(any()) } returns null

        val gitHubClientFactory = mockk<OntrackGitHubClientFactory>()
        val gitHubClient = mockk<OntrackGitHubClient>()
        every { gitHubClientFactory.create(any()) } returns gitHubClient
        if (pathFound) {
            val path = if (incorrectConfig) {
                "/ingestion/config-incorrect.yml"
            } else {
                "/ingestion/config.yml"
            }
            every { gitHubClient.getFileContent("org/repo", "release/1.0", INGESTION_CONFIG_FILE_PATH) } returns
                    TestUtils.resourceBytes(path)
        } else {
            every { gitHubClient.getFileContent("org/repo", "release/1.0", INGESTION_CONFIG_FILE_PATH) } returns null
        }

        val propertyService = mockk<PropertyService>()
        if (projectConfigured) {
            every { propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java) } returns
                    Property.of(
                        mockk<GitHubProjectConfigurationPropertyType>(),
                        GitHubProjectConfigurationProperty(
                            GitHubEngineConfiguration("test", null, oauth2Token = "some-token"),
                            "org/repo",
                            30,
                            issueServiceConfigurationIdentifier = null,
                        )
                    )
        } else {
            every { propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java) } returns
                    Property.of(mockk<GitHubProjectConfigurationPropertyType>(), null)
        }
        if (branchConfigured) {
            every { propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java) } returns
                    Property.of(
                        mockk<GitBranchConfigurationPropertyType>(),
                        GitBranchConfigurationProperty(
                            branch = "release/1.0",
                            buildCommitLink = null,
                            isOverride = false,
                            buildTagInterval = 0,
                        )
                    )
        } else {
            every { propertyService.getProperty(branch, GitBranchConfigurationPropertyType::class.java) } returns
                    Property.of(
                        mockk<GitBranchConfigurationPropertyType>(),
                        null
                    )
        }

        val configLoaderService = DefaultConfigLoaderService(
            gitHubClientFactory = gitHubClientFactory,
            propertyService = propertyService,
            gitService = gitService,
        )
        val config = configLoaderService.loadConfig(branch, INGESTION_CONFIG_FILE_PATH)
        if (expectedConfig) {
            assertNotNull(config, "Configuration was loaded")
        } else {
            assertNull(config, "Configuration could not be found")
        }
    }

}