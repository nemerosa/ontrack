package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfigWorkflows
import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigLoaderService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigLoaderServiceITMockConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunDecorator
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull
import kotlin.test.*

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class WorkflowRunIngestionEventProcessorIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var processor: WorkflowRunIngestionEventProcessor

    @Autowired
    private lateinit var buildGitHubWorkflowRunDecorator: BuildGitHubWorkflowRunDecorator

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @BeforeEach
    fun init() {
        ConfigLoaderServiceITMockConfig.defaultIngestionConfig(configLoaderService)
    }

    @Test
    fun `Payload source for a PR`() {
        val repo = uid("r")
        val payload: WorkflowRunPayload = IngestionHookFixtures.sampleWorkflowRunPayload(
            repoName = repo,
            pullRequest = IngestionHookFixtures.sampleWorkflowRunPR(
                repoName = repo,
                number = 13,
            ),
            runName = "ci",
            runNumber = 26,
            runId = 12345L,
        )
        assertEquals(
            "12345",
            processor.getPayloadSource(payload)
        )
    }

    @Test
    fun `Payload source for a branch`() {
        val payload = payload(
            runId = 1234L,
            runName = "ci",
            runNumber = 26,
        )
        assertEquals(
            "1234",
            processor.getPayloadSource(payload)
        )
    }

    @Test
    fun `Workflow run for a PR`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Starting a run
        val repo = uid("r")
        val owner = IngestionHookFixtures.sampleOwner
        asAdmin {
            withGitHubIngestionSettings {
                val payload: WorkflowRunPayload = IngestionHookFixtures.sampleWorkflowRunPayload(
                    repoName = repo,
                    pullRequest = IngestionHookFixtures.sampleWorkflowRunPR(
                        repoName = repo,
                    )
                )
                processor.process(
                    payload,
                    null
                )
                assertNotNull(structureService.findProjectByName(repo).getOrNull()) { project ->
                    assertNotNull(
                        getProperty(project, GitHubProjectConfigurationPropertyType::class.java),
                        "GitHub config set on project"
                    ) {
                        assertEquals(config.name, it.configuration.name)
                        assertEquals("$owner/$repo", it.repository)
                        assertEquals(30, it.indexationInterval)
                        assertEquals("self", it.issueServiceConfigurationIdentifier)
                    }
                    assertNotNull(structureService.findBranchByName(project.name, "PR-1").getOrNull()) { branch ->
                        assertFalse(branch.isDisabled, "PR branch is not disabled")
                        assertNotNull(
                            getProperty(branch, GitBranchConfigurationPropertyType::class.java),
                            "Git config set on branch"
                        ) {
                            assertEquals("PR-1", it.branch)
                            assertNotNull(it.buildCommitLink) { link ->
                                assertEquals("git-commit-property", link.id)
                            }
                        }
                        assertNotNull(
                            structureService.findBuildByName(project.name, branch.name, "ci-1").getOrNull(),
                            "PR build has been created"
                        ) { build ->
                            // Build commit property
                            assertNotNull(
                                getProperty(build, GitCommitPropertyType::class.java),
                                "Git commit property set on build"
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `No validation for the workflow run using the ingestion configuration`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Starting the run
        asAdmin {
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig(
                        workflows = IngestionConfigWorkflows(filter = FilterConfig.none)
                    )
                )
                workflowRunValidationTest(expectProject = false)
            }
        }
    }

    @Test
    fun `No validation for the workflow run using exclusion of branches in the configuration`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Starting the run
        asAdmin {
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig(
                        workflows = IngestionConfigWorkflows(
                            branchFilter = FilterConfig(excludes = "main")
                        )
                    )
                )
                workflowRunValidationTest(expectProject = false)
            }
        }
    }

    @Test
    fun `No validation for the workflow run using exclusion of pull requests in the configuration`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Starting the run
        asAdmin {
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig(
                        workflows = IngestionConfigWorkflows(
                            includePRs = false,
                        )
                    )
                )
                workflowRunValidationTest(
                    pullRequests = listOf(
                        IngestionHookFixtures.sampleWorkflowRunPR(
                            repoName = IngestionHookFixtures.sampleRepository,
                        )
                    ),
                    expectProject = false,
                )
            }
        }
    }

    @Test
    fun `Validation for the workflow run using the ingestion configuration`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Starting the run
        asAdmin {
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig()
                )
                workflowRunValidationTest()
            }
        }
    }

    @Test
    fun `No processing of the workflow run when in progress`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // Starting the run
        asAdmin {
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig()
                )
                workflowRunValidationTest(action = WorkflowRunAction.in_progress, expectProject = false)
            }
        }
    }

    private fun workflowRunValidationTest(
        action: WorkflowRunAction = WorkflowRunAction.completed,
        expectProject: Boolean = true,
        expectValidation: Boolean = true,
        pullRequests: List<WorkflowRunPullRequest> = emptyList(),
    ) {
        val payload = payload(
            action = action,
            status = WorkflowJobStepStatus.completed,
            conclusion = WorkflowJobStepConclusion.success,
            pullRequests = pullRequests,
        )
        processor.process(
            payload,
            null
        )
        if (expectProject) {
            assertNotNull(structureService.findProjectByName(payload.repository.name).getOrNull()) { project ->
                assertNotNull(
                    structureService.findBranchByName(project.name, payload.workflowRun.headBranch).getOrNull()
                ) { branch ->
                    if (expectValidation) {
                        assertNotNull(
                            structureService.findValidationStampByName(branch.project.name, branch.name, "workflow-CI")
                                .getOrNull()
                        ) { vs ->
                            assertNotNull(
                                structureService.findBuildByName(project.name, branch.name, "ci-1").getOrNull()
                            ) { build ->
                                val runs =
                                    structureService.getValidationRunsForBuildAndValidationStamp(build.id, vs.id, 0, 10)
                                assertEquals(1, runs.size)
                                val run = runs.first()
                                assertEquals(ValidationRunStatusID.STATUS_PASSED, run.lastStatus.statusID)
                            }
                        }
                    } else {
                        assertNull(
                            structureService.findValidationStampByName(branch.project.name, branch.name, "workflow-ci")
                                .getOrNull()
                        )
                    }
                }
            }
        } else {
            assertNull(
                structureService.findProjectByName(payload.repository.name).getOrNull(),
                "Project has not been created"
            )
        }
    }

    @Test
    fun `Same build targeted by two worflows based on Git commit`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // For a given project & branch
        asAdmin {
            project {
                branch("main") {
                    val commit = uid("commit-")
                    // First workflow with a given commit will create the build and
                    // link the workflow to it (and the Git commit)
                    processor.process(
                        payload(
                            repoName = project.name,
                            runName = "build",
                            runNumber = 20,
                            runId = 10L,
                            commit = commit,
                        ),
                        configuration = null
                    )
                    // Checks the build has been created
                    val build = structureService.findBuildByName(project.name, name, "build-20").orElse(null)
                        ?: fail("Build created by first workflow")
                    // Checks the run ID has been set on the build
                    assertNotNull(
                        getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java),
                        "Workflow run property set on the build"
                    ) {
                        assertNotNull(it.findRun(10L), "First workflow run ID set on the build")
                    }
                    // Checks the Git commit has been set
                    assertNotNull(
                        getProperty(build, GitCommitPropertyType::class.java),
                        "Git commit property set on the build"
                    ) {
                        assertEquals(commit, it.commit)
                    }

                    // TODO Second workflow with the same commit will reuse the previous build and
                    // link the new workflow to it
                    processor.process(
                        payload(
                            repoName = project.name,
                            runName = "tests",
                            runNumber = 15,
                            runId = 25L,
                            commit = commit, // <-- same commit
                        ),
                        configuration = null
                    )
                    // Checks the new run ID has been set on the build
                    assertNotNull(
                        getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java),
                        "New workflow run property set on the build"
                    ) {
                        assertNotNull(it.findRun(25L), "Second workflow run ID set on the build")
                    }
                    // Checks that no other build was created
                    assertNull(
                        structureService.findBuildByName(project.name, name, "tests-25").orElse(null),
                        "Second workflow did not create any build"
                    )
                }
            }
        }
    }

    @Test
    fun `Default commit build id strategy not creating a build if Git commit is absent`() {
        // Only one GitHub configuration
        onlyOneGitHubConfig()
        // For a given project & branch
        asAdmin {
            project {
                branch("main") {
                    processor.process(
                        payload(
                            repoName = project.name,
                            runName = "build",
                            runNumber = 20,
                            runId = 10L,
                            commit = "", // <- no commit
                        ),
                        configuration = null
                    )
                    // Checks the build has NOT been created
                    assertNull(
                        structureService.findBuildByName(project.name, name, "build-20").orElse(null),
                        "Build not created because no commit"
                    )
                }
            }
        }
    }

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
                    conclusion = null,
                    runNumber = 1,
                    headBranch = "release/1.0",
                    repoName = repoName,
                    owner = owner,
                    sender = owner,
                    commit = commit,
                ),
                configuration = null,
            )
            // Checks the build is running
            assertNotNull(
                structureService.findBuildByName(projectName, branchName, buildName).getOrNull(),
                "Build created"
            ) { build ->
                assertNotNull(
                    getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java)?.workflows?.firstOrNull(),
                    "GitHub Workflow link set"
                ) { link ->
                    assertTrue(link.running, "Workflow is running")
                }
            }
            // Sending a completed event
            processor.process(
                payload(
                    action = WorkflowRunAction.completed,
                    conclusion = WorkflowJobStepConclusion.success,
                    runNumber = 1,
                    headBranch = "release/1.0",
                    repoName = repoName,
                    owner = owner,
                    sender = owner,
                    commit = commit,
                ),
                configuration = null,
            )
            // Checks the build is not running any longer
            assertNotNull(
                structureService.findBuildByName(projectName, branchName, buildName).getOrNull(),
                "Build created"
            ) { build ->
                assertNotNull(
                    getProperty(build, BuildGitHubWorkflowRunPropertyType::class.java)?.workflows?.firstOrNull(),
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
                ),
                configuration = null,
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
                ),
                configuration = null,
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

    @Test
    fun `Tags are not processed for the workflows`() {
        asAdmin {
            withGitHubIngestionSettings {
                val payload: WorkflowRunPayload = IngestionHookFixtures.sampleWorkflowRunPayload(
                    headBranch = "refs/tags/1.2.3"
                )
                assertEquals(
                    IngestionEventPreprocessingCheck.IGNORED,
                    processor.preProcessingCheck(payload)
                )
            }
        }
    }

    @Test
    fun `Workflows can be ignored`() {
        asAdmin {
            onlyOneGitHubConfig()
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig(
                        workflows = IngestionConfigWorkflows(
                            filter = FilterConfig(
                                includes = ".*",
                                excludes = ".*ignored.*",
                            )
                        )
                    )
                )
                val repoName = uid("r")
                val payload: WorkflowRunPayload = IngestionHookFixtures.sampleWorkflowRunPayload(
                    repoName = repoName,
                    runName = "to-be-ignored"
                )
                assertEquals(
                    IngestionEventProcessingResult.IGNORED,
                    processor.process(payload, null).result
                )
                // Checks the project has not been created
                assertNull(
                    structureService.findProjectByName(repoName).getOrNull(),
                    "Project has not been created"
                )
            }
        }
    }

    @Test
    fun `Only push is supported by default, subsequent jobs and steps are ignored`() {
        asAdmin {
            onlyOneGitHubConfig()
            withGitHubIngestionSettings {
                ConfigLoaderServiceITMockConfig.customIngestionConfig(
                    configLoaderService, IngestionConfig()
                )
                val repoName = uid("r")
                val payload: WorkflowRunPayload = IngestionHookFixtures.sampleWorkflowRunPayload(
                    repoName = repoName,
                    event = "workflow_dispatch", // Workflow triggered manually ignoring
                )
                assertEquals(
                    IngestionEventProcessingResult.IGNORED,
                    processor.process(payload, null).result
                )
                // Checks the project & the branch have not been created
                assertNull(
                    structureService.findProjectByName(repoName).getOrNull(),
                    "Project has not been created"
                )
            }
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
            processor.process(payload, configuration = null)
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
                            getProperty(
                                build,
                                BuildGitHubWorkflowRunPropertyType::class.java
                            )?.workflows?.firstOrNull(),
                            "GitHub workflow run URL"
                        ) {
                            assertEquals(htmlUrl, it.url)
                            assertEquals("CI", it.name)
                            assertEquals(1, it.runNumber)
                        }
                        // Build link to the run as a decoration
                        val decorations = buildGitHubWorkflowRunDecorator.getDecorations(build)
                        assertEquals(1, decorations.size)
                        val decoration = decorations.first().data.workflows.first()
                        assertEquals(htmlUrl, decoration.url)
                        assertEquals("CI", decoration.name)
                        assertEquals(1, decoration.runNumber)
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
        action: WorkflowRunAction = WorkflowRunAction.completed,
        runName: String = "CI",
        runNumber: Int = 1,
        headBranch: String = "main",
        createdAtDate: LocalDateTime = Time.now(),
        updatedAtDate: LocalDateTime? = Time.now().plusSeconds(10),
        repoName: String = IngestionHookFixtures.sampleRepository,
        repoDescription: String = "Repository $repoName",
        owner: String = IngestionHookFixtures.sampleOwner,
        sender: String = IngestionHookFixtures.sampleOwner,
        commit: String = "some-commit",
        htmlUrl: String = "https://github.com/$owner/$repoName/actions/runs/1395528922",
        repoUrl: String = "https://github.com/$owner/$repoName",
        status: WorkflowJobStepStatus = WorkflowJobStepStatus.in_progress,
        conclusion: WorkflowJobStepConclusion? = null,
        pullRequests: List<WorkflowRunPullRequest> = emptyList(),
    ) = WorkflowRunPayload(
        action = action,
        workflowRun = WorkflowRun(
            id = runId,
            name = runName,
            runNumber = runNumber,
            pullRequests = pullRequests,
            headBranch = headBranch,
            headSha = commit,
            createdAtDate = createdAtDate,
            htmlUrl = htmlUrl,
            updatedAtDate = updatedAtDate,
            event = "push",
            status = status,
            conclusion = conclusion,
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