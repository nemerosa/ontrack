package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Owner
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class WorkflowRunIngestionEventProcessorIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var processor: WorkflowRunIngestionEventProcessor

    @Test
    fun `Setting up the build`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Payload
        val repoName = uid("R")
        val owner = uid("o")
        val commit = "1234567890"
        val payload = payload(
            action = WorkflowRunAction.requested,
            runNumber = 1,
            headBranch = "release/1.0",
            repoName = repoName,
            owner = owner,
            sender = owner,
            commit = commit,
        )
        // Processing
        asAdmin {
            processor.process(payload)
        }
        // Checks the project, branch & build
        val projectName = "$owner-$repoName"
        val branchName = "release-1.0"
        val buildName = "CI-1"
        asAdmin {
            assertNotNull(structureService.findProjectByName(projectName).getOrNull()) { project ->
                assertNotNull(getProperty(project, GitHubProjectConfigurationPropertyType::class.java), "GitHub config set on project") {
                    assertEquals(config.name, it.configuration.name)
                    assertEquals("$owner/$repoName", it.repository)
                    assertEquals(30, it.indexationInterval)
                    assertEquals("self", it.issueServiceConfigurationIdentifier)
                }
                assertNotNull(structureService.findBranchByName(project.name, branchName).getOrNull()) { branch ->
                    assertNotNull(getProperty(branch, GitBranchConfigurationPropertyType::class.java), "Git config set on branch") {
                        assertEquals("release/1.0", it.branch)
                        assertNotNull(it.buildCommitLink) { link ->
                            assertEquals("git-commit-property", link.id)
                        }
                    }
                    assertNotNull(
                        structureService.findBuildByName(project.name, branch.name, buildName).getOrNull()
                    ) { build ->
                        assertNotNull(getProperty(build, GitCommitPropertyType::class.java), "Git commit property set on build") {
                            assertEquals(commit, it.commit)
                        }
                    }
                }
            }
        }
    }

    private fun onlyOneGitHubConfig(): GitHubEngineConfiguration =
        asAdmin {
            // Removing all previous configuration
            gitConfigurationService.configurations.forEach {
                gitConfigurationService.deleteConfiguration(it.name)
            }
            // Creating one configuration
            gitHubConfig()
        }

    private fun payload(
        action: WorkflowRunAction,
        runNumber: Int,
        headBranch: String,
        createdAtDate: LocalDateTime = Time.now(),
        repoName: String,
        repoDescription: String = "Repository $repoName",
        owner: String,
        sender: String,
        commit: String,
    ) = WorkflowRunPayload(
        action = action,
        workflowRun = WorkflowRun(
            name = "CI",
            runNumber = runNumber,
            pullRequests = emptyList(),
            headBranch = headBranch,
            headSha = commit,
            createdAtDate = createdAtDate,
        ),
        repository = Repository(
            name = repoName,
            description = repoDescription,
            owner = Owner(login = owner),
        ),
        sender = User(login = sender)
    )

}