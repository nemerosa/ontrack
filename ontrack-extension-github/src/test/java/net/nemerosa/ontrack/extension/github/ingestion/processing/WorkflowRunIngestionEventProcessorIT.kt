package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRun
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunAction
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRunPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Owner
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.User
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunDecorator
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.*

class WorkflowRunIngestionEventProcessorIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var processor: WorkflowRunIngestionEventProcessor

    @Autowired
    private lateinit var buildGitHubWorkflowRunDecorator: BuildGitHubWorkflowRunDecorator

    @Autowired
    private lateinit var runInfoService: RunInfoService

    @Test
    fun `Build workflow run link running state`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Payload
        val repoName = uid("R")
        val owner = uid("o")
        val commit = "1234567890"
        val projectName = normalizeName(repoName)
        val branchName = "release-1.0"
        val buildName = "ci-1"
        // Starting the run
        asAdmin {
            processor.process(
                payload(
                    action = WorkflowRunAction.requested,
                    runNumber = 1,
                    headBranch = "release/1.0",
                    repoName = repoName,
                    owner = owner,
                    sender = owner,
                    commit = commit,
                )
            )
            // Checks the build is running
            assertNotNull(
                structureService.findBuildByName(projectName, branchName, buildName).getOrNull(),
                "Build created"
            ) { build ->
                assertNotNull(
                    getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java),
                    "GitHub Workflow link set"
                ) { link ->
                    assertTrue(link.running, "Workflow is running")
                }
            }
            // Sending a completed event
            processor.process(
                payload(
                    action = WorkflowRunAction.completed,
                    runNumber = 1,
                    headBranch = "release/1.0",
                    repoName = repoName,
                    owner = owner,
                    sender = owner,
                    commit = commit,
                )
            )
            // Checks the build is not running any longer
            assertNotNull(
                structureService.findBuildByName(projectName, branchName, buildName).getOrNull(),
                "Build created"
            ) { build ->
                assertNotNull(
                    getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java),
                    "GitHub Workflow link set"
                ) { link ->
                    assertFalse(link.running, "Workflow is not running")
                }
            }
        }
    }

    @Test
    fun `Build run info`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Payload
        val repoName = uid("R")
        val owner = uid("o")
        val commit = "1234567890"
        val projectName = normalizeName(repoName)
        val branchName = "release-1.0"
        val buildName = "ci-1"
        // Starting the run
        val ref = Time.now()
        asAdmin {
            processor.process(
                payload(
                    action = WorkflowRunAction.requested,
                    runNumber = 1,
                    headBranch = "release/1.0",
                    repoName = repoName,
                    owner = owner,
                    sender = owner,
                    commit = commit,
                    createdAtDate = ref.minusMinutes(5),
                )
            )
            // Checks the run info is not there
            assertNotNull(
                structureService.findBuildByName(projectName, branchName, buildName).getOrNull(),
                "Build created"
            ) { build ->
                assertNull(
                    runInfoService.getRunInfo(build),
                    "No run info yet"
                )
            }
            // Sending a completed event
            processor.process(
                payload(
                    action = WorkflowRunAction.completed,
                    runNumber = 1,
                    headBranch = "release/1.0",
                    repoName = repoName,
                    owner = owner,
                    sender = owner,
                    commit = commit,
                    createdAtDate = ref.minusMinutes(5),
                    updatedAtDate = ref,
                )
            )
            // Checks the run info is filled in
            assertNotNull(
                structureService.findBuildByName(projectName, branchName, buildName).getOrNull(),
                "Build created"
            ) { build ->
                assertNotNull(
                    runInfoService.getRunInfo(build),
                    "No run info yet"
                ) { info ->
                    assertEquals(300, info.runTime)
                }
            }
        }
    }

    @Test
    fun `Setting up the build with one unique GitHub configuration`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        basicTest(config)
    }

    @Test
    fun `Setting up the build with one mismatch GitHub configuration`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        assertFailsWith<GitHubConfigURLMismatchException> {
            basicTest(
                config,
                htmlUrl = "https://github.enterprise.com/nemerosa/github-ingestion-poc/actions/runs/1395528922",
                repoUrl = "https://github.enterprise.com/nemerosa/github-ingestion-poc",
            )
        }
    }

    @Test
    fun `Setting up the build with no GitHub configuration`() {
        // Only one GitHub configuration
        noGitHubConfig()
        // Runs the test
        assertFailsWith<NoGitHubConfigException> {
            basicTest(null)
        }
    }

    @Test
    fun `Setting up the build with several GitHub configurations and none matching`() {
        // Only one GitHub configuration
        severalGitHubConfigs()
        // Runs the test
        assertFailsWith<GitHubConfigURLNoMatchException> {
            basicTest(
                null,
                htmlUrl = "https://github.enterprise0.com/nemerosa/github-ingestion-poc/actions/runs/1395528922"
            )
        }
    }

    @Test
    fun `Setting up the build with several GitHub configurations and one matching`() {
        // Only one GitHub configuration
        val match = severalGitHubConfigs()
        // Runs the test
        basicTest(
            match,
            htmlUrl = "${match.url}/nemerosa/github-ingestion-poc/actions/runs/1395528922",
            repoUrl = "${match.url}/nemerosa/github-ingestion-poc",
        )
    }

    @Test
    fun `Setting up the build with several GitHub configurations and several matching`() {
        // Only one GitHub configuration
        val match = severalGitHubConfigs(sameRoot = true)
        // Runs the test
        assertFailsWith<GitHubConfigURLSeveralMatchesException> {
            basicTest(
                match,
                htmlUrl = "${match.url}/nemerosa/github-ingestion-poc/actions/runs/1395528922",
                repoUrl = "${match.url}/nemerosa/github-ingestion-poc",
            )
        }
    }

    private fun basicTest(
        config: GitHubEngineConfiguration?,
        htmlUrl: String = "https://github.com/nemerosa/github-ingestion-poc/actions/runs/1395528922",
        repoUrl: String = "https://github.com/nemerosa/github-ingestion-poc",
    ) {
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
            htmlUrl = htmlUrl,
            repoUrl = repoUrl,
        )
        // Processing
        asAdmin {
            processor.process(payload)
        }
        // Checks the project, branch & build
        val projectName = normalizeName(repoName)
        val branchName = "release-1.0"
        val buildName = "ci-1"
        asAdmin {
            assertNotNull(structureService.findProjectByName(projectName).getOrNull()) { project ->
                assertNotNull(
                    getProperty(project, GitHubProjectConfigurationPropertyType::class.java),
                    "GitHub config set on project"
                ) {
                    assertEquals(config?.name, it.configuration.name)
                    assertEquals("$owner/$repoName", it.repository)
                    assertEquals(30, it.indexationInterval)
                    assertEquals("self", it.issueServiceConfigurationIdentifier)
                }
                assertNotNull(structureService.findBranchByName(project.name, branchName).getOrNull()) { branch ->
                    assertNotNull(
                        getProperty(branch, GitBranchConfigurationPropertyType::class.java),
                        "Git config set on branch"
                    ) {
                        assertEquals("release/1.0", it.branch)
                        assertNotNull(it.buildCommitLink) { link ->
                            assertEquals("git-commit-property", link.id)
                        }
                    }
                    assertNotNull(
                        structureService.findBuildByName(project.name, branch.name, buildName).getOrNull()
                    ) { build ->
                        // Build link to the run
                        assertNotNull(
                            getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java),
                            "GitHub workflow run URL"
                        ) {
                            assertEquals(htmlUrl, it.url)
                            assertEquals("CI", it.name)
                            assertEquals(1, it.runNumber)
                        }
                        // Build link to the run as a decoration
                        val decorations = buildGitHubWorkflowRunDecorator.getDecorations(build)
                        assertEquals(1, decorations.size)
                        val decoration = decorations.first()
                        assertEquals(htmlUrl, decoration.data.url)
                        assertEquals("CI", decoration.data.name)
                        assertEquals(1, decoration.data.runNumber)
                        // Build commit property
                        assertNotNull(
                            getProperty(build, GitCommitPropertyType::class.java),
                            "Git commit property set on build"
                        ) {
                            assertEquals(commit, it.commit)
                        }
                    }
                }
            }
        }
    }

    private fun payload(
        runId: Long = 1,
        action: WorkflowRunAction,
        runNumber: Int,
        headBranch: String,
        createdAtDate: LocalDateTime = Time.now(),
        updatedAtDate: LocalDateTime? = null,
        repoName: String,
        repoDescription: String = "Repository $repoName",
        owner: String,
        sender: String,
        commit: String,
        htmlUrl: String = "https://github.com/nemerosa/github-ingestion-poc/actions/runs/1395528922",
        repoUrl: String = "https://github.com/nemerosa/github-ingestion-poc",
    ) = WorkflowRunPayload(
        action = action,
        workflowRun = WorkflowRun(
            id = runId,
            name = "CI",
            runNumber = runNumber,
            pullRequests = emptyList(),
            headBranch = headBranch,
            headSha = commit,
            createdAtDate = createdAtDate,
            htmlUrl = htmlUrl,
            updatedAtDate = updatedAtDate,
            event = "push",
        ),
        repository = Repository(
            name = repoName,
            description = repoDescription,
            owner = Owner(login = owner),
            htmlUrl = repoUrl,
        ),
        sender = User(login = sender)
    )

}