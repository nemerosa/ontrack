package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.processing.GitHubConfigURLMismatchException
import net.nemerosa.ontrack.extension.github.ingestion.processing.GitHubConfigURLNoMatchException
import net.nemerosa.ontrack.extension.github.ingestion.processing.GitHubConfigURLSeveralMatchesException
import net.nemerosa.ontrack.extension.github.ingestion.processing.NoGitHubConfigException
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Owner
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class IngestionModelAccessServiceIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionModelAccessService: IngestionModelAccessService

    @Test
    fun `Setting up the project with one unique GitHub configuration`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        testProject(config)
    }

    @Test
    fun `Setting up the build with one mismatch GitHub configuration`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        assertFailsWith<GitHubConfigURLMismatchException> {
            testProject(
                config,
                htmlUrl = "https://github.enterprise.com/nemerosa/github-ingestion-poc",
            )
        }
    }

    @Test
    fun `Setting up the build with no GitHub configuration`() {
        // Only one GitHub configuration
        noGitHubConfig()
        // Runs the test
        assertFailsWith<NoGitHubConfigException> {
            testProject(null)
        }
    }

    @Test
    fun `Setting up the build with several GitHub configurations and none matching`() {
        // Only one GitHub configuration
        severalGitHubConfigs()
        // Runs the test
        assertFailsWith<GitHubConfigURLNoMatchException> {
            testProject(
                null,
                htmlUrl = "https://github.enterprise0.com/nemerosa/github-ingestion-poc"
            )
        }
    }

    @Test
    fun `Setting up the build with several GitHub configurations and one matching`() {
        // Only one GitHub configuration
        val match = severalGitHubConfigs()
        // Runs the test
        testProject(
            match,
            htmlUrl = "${match.url}/nemerosa/github-ingestion-poc",
        )
    }

    @Test
    fun `Setting up the build with several GitHub configurations and several matching`() {
        // Only one GitHub configuration
        val match = severalGitHubConfigs(sameRoot = true)
        // Runs the test
        assertFailsWith<GitHubConfigURLSeveralMatchesException> {
            testProject(
                match,
                htmlUrl = "${match.url}/nemerosa/github-ingestion-poc",
            )
        }
    }

    @Test
    fun `Creating a branch`() {
        project {
            val branch = ingestionModelAccessService.getOrCreateBranch(this, "release/1.0", null)
            assertNotNull(
                structureService.findBranchByName(project.name, "release-1.0").getOrNull(),
                "Branch created"
            ) {
                assertEquals(branch.id, it.id, "Same branch")
            }
        }
    }

    @Test
    fun `Keeping a branch`() {
        project {
            branch("release-1.0") {
                val branch = ingestionModelAccessService.getOrCreateBranch(project, "release/1.0", null)
                assertEquals(this.id, branch.id, "Same branch")
            }
        }
    }

    @Test
    fun `Configuration of the issue service identifier`() {
        val config = onlyOneGitHubConfig()
        testProject(config, issueServiceIdentifier = "jira//config")
    }

    @Test
    fun `Configuration of the indexation interval`() {
        val config = onlyOneGitHubConfig()
        testProject(config, indexationInterval = 60)
    }

    private fun testProject(
        config: GitHubEngineConfiguration?,
        owner: String = "nemerosa",
        name: String = "github-ingestion-poc",
        htmlUrl: String = "${config?.url ?: "https://github.com"}/$owner/$name",
        issueServiceIdentifier: String = "self",
        indexationInterval: Int = 30,
    ) {
        withGitHubIngestionSettings(
            orgProjectPrefix = false,
            issueServiceIdentifier = issueServiceIdentifier,
            indexationInterval = indexationInterval,
        ) {
            asAdmin {
                ingestionModelAccessService.getOrCreateProject(
                    Repository(
                        name = name,
                        description = null,
                        owner = Owner(login = owner),
                        htmlUrl = htmlUrl,
                    )
                )
                val projectName = normalizeName(name)
                assertNotNull(structureService.findProjectByName(projectName).getOrNull()) { project ->
                    assertNotNull(
                        getProperty(project, GitHubProjectConfigurationPropertyType::class.java),
                        "GitHub config set on project"
                    ) {
                        assertEquals(config?.name, it.configuration.name)
                        assertEquals("$owner/$name", it.repository)
                        assertEquals(indexationInterval, it.indexationInterval)
                        assertEquals(issueServiceIdentifier, it.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

}