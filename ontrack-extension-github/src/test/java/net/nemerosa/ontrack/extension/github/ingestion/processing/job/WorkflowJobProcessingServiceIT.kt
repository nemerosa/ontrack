package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.*
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobPropertyType
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.RunInfoService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class WorkflowJobProcessingServiceIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var workflowJobProcessingService: WorkflowJobProcessingService

    @Autowired
    private lateinit var runInfoService: RunInfoService

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @BeforeEach
    fun before() {
        onlyOneGitHubConfig()
        ConfigLoaderServiceITMockConfig.defaultIngestionConfig(configLoaderService)
    }

    @Test
    fun `Creation of a simple validation run`() {
        project {
            branch {
                build {
                    runTest()
                }
            }
        }
    }

    @Test
    fun `Creation of a simple validation run with a very large ID`() {
        project {
            branch {
                build {
                    runTest(runId = 2218938646L)
                }
            }
        }
    }

    @Test
    fun `Excluding a step based on the global settings for step exclusion`() {
        withGitHubIngestionSettings(stepExcludes = "publishing.*") {
            project {
                branch {
                    build {
                        runTest(expectedStep = false)
                    }
                }
            }
        }
    }

    @Test
    fun `Excluding a step based on the global settings for job exclusion`() {
        withGitHubIngestionSettings(jobExcludes = "build") {
            project {
                branch {
                    build {
                        runTest(expectedStep = false)
                    }
                }
            }
        }
    }

    @Test
    fun `Excluding a step based on the ingestion config for step exclusion`() {
        ConfigLoaderServiceITMockConfig.customIngestionConfig(
            configLoaderService,
            IngestionConfig(
                stepsFilter = FilterConfig(excludes = "publishing.*")
            )
        )
        withGitHubIngestionSettings {
            project {
                branch {
                    build {
                        runTest(expectedStep = false)
                    }
                }
            }
        }
    }

    @Test
    fun `Excluding a step based on the ingestion config for job exclusion`() {
        ConfigLoaderServiceITMockConfig.customIngestionConfig(
            configLoaderService,
            IngestionConfig(
                jobsFilter = FilterConfig(excludes = "build")
            )
        )
        withGitHubIngestionSettings {
            project {
                branch {
                    build {
                        runTest(expectedStep = false)
                    }
                }
            }
        }
    }

    @Test
    fun `Creation of a simple validation run and the associated job`() {
        ConfigLoaderServiceITMockConfig.customIngestionConfig(
            configLoaderService,
            IngestionConfig(
                general = IngestionConfigGeneral(
                    skipJobs = false,
                ),
            )
        )
        project {
            branch {
                build {
                    runTest(step = null, expectedStep = false, expectedJob = true)
                }
            }
        }
    }

    @Test
    fun `Exclusion of job based on settings`() {
        withGitHubIngestionSettings(jobExcludes = "build") {
            ConfigLoaderServiceITMockConfig.defaultIngestionConfig(configLoaderService)
            project {
                branch {
                    build {
                        runTest(step = null, expectedStep = false, expectedJob = false)
                    }
                }
            }
        }
    }

    @Test
    fun `Exclusion of job based on the ingestion config`() {
        withGitHubIngestionSettings {
            ConfigLoaderServiceITMockConfig.customIngestionConfig(
                configLoaderService,
                IngestionConfig(
                    jobsFilter = FilterConfig(excludes = "build")
                )
            )
            project {
                branch {
                    build {
                        runTest(step = null, expectedStep = false, expectedJob = false)
                    }
                }
            }
        }
    }

    @Test
    fun `Failed status`() {
        project {
            branch {
                build {
                    runTest(
                        conclusion = WorkflowJobStepConclusion.failure,
                        expectedStatus = "FAILED",
                    )
                }
            }
        }
    }

    @Test
    fun `Creation of a simple validation run twice`() {
        project {
            branch {
                build {
                    runTest(runId = 1)
                }
                build {
                    runTest(runId = 2)
                }
            }
        }
    }

    @Test
    fun `Job progressing on several payloads`() {
        project {
            branch {
                build {
                    setupTest(
                        status = WorkflowJobStepStatus.queued,
                        conclusion = null,
                    )
                    setupTest(
                        status = WorkflowJobStepStatus.in_progress,
                        conclusion = null,
                    )
                    setupTest(
                        status = WorkflowJobStepStatus.completed,
                        conclusion = WorkflowJobStepConclusion.success,
                    )
                    setupTest(
                        status = WorkflowJobStepStatus.completed,
                        conclusion = WorkflowJobStepConclusion.success,
                    )
                    assertNotNull(
                        structureService.findValidationStampByName(
                            project.name,
                            branch.name,
                            "build-publishing-to-the-repository"
                        ).getOrNull(),
                        "Validation stamp has been created"
                    ) { vs ->
                        val runs = structureService.getValidationRunsForBuildAndValidationStamp(
                            id,
                            vs.id,
                            offset = 0,
                            count = 1
                        )
                        assertEquals(1, runs.size, "One and only one run created")
                        val run = runs.first()
                        assertEquals("PASSED", run.lastStatusId)
                        assertNotNull(runInfoService.getRunInfo(run), "Run info has been set") { info ->
                            assertEquals(60, info.runTime, "Run time = 60 seconds")
                            assertEquals("github-workflow", info.sourceType)
                            assertEquals("uri:job", info.sourceUri)
                        }
                        assertNotNull(getProperty(run, ValidationRunGitHubWorkflowJobPropertyType::class.java)) { p ->
                            assertEquals("build", p.job)
                            assertEquals("run-name", p.name)
                            assertEquals(1, p.runNumber)
                            assertEquals("uri:job", p.url)
                            assertEquals(false, p.running)
                        }
                    }
                }
            }
        }
    }

    private fun Build.runTest(
        runId: Long = 1L,
        job: String = "build",
        step: String? = "Publishing to the repository",
        status: WorkflowJobStepStatus = WorkflowJobStepStatus.completed,
        conclusion: WorkflowJobStepConclusion? = WorkflowJobStepConclusion.success,
        expectedStep: Boolean = true,
        expectedVsName: String = normalizeName("$job-$step"),
        expectedStatus: String = "PASSED",
        expectedJob: Boolean = false,
        expectedJobVsName: String = normalizeName(job),
    ) {
        setupTest(
            runId = runId,
            job = job,
            step = step,
            status = status,
            conclusion = conclusion,
        )
        asAdmin {
            if (expectedStep) {
                // Checks the validation stamp has been created
                assertNotNull(
                    structureService.findValidationStampByName(project.name, branch.name, expectedVsName).getOrNull(),
                    "Validation stamp has been created"
                ) { vs ->
                    // Checks the validation run has been created
                    val runs = structureService.getValidationRunsForBuildAndValidationStamp(
                        id,
                        vs.id,
                        offset = 0,
                        count = 1
                    )
                    assertNotNull(runs.firstOrNull()) { run ->
                        assertEquals(expectedStatus, run.lastStatusId)
                        assertNotNull(runInfoService.getRunInfo(run), "Run info has been set") { info ->
                            assertEquals(60, info.runTime, "Run time = 60 seconds")
                            assertEquals("github-workflow", info.sourceType)
                            assertEquals("uri:job", info.sourceUri)
                        }
                        assertNotNull(getProperty(run, ValidationRunGitHubWorkflowJobPropertyType::class.java)) { p ->
                            assertEquals("build", p.job)
                            assertEquals("run-name", p.name)
                            assertEquals(1, p.runNumber)
                            assertEquals("uri:job", p.url)
                            assertEquals(false, p.running)
                        }
                    }
                }
            } else {
                // Checks the validation stamp has NOT been created
                assertNull(
                    structureService.findValidationStampByName(project.name, branch.name, expectedVsName).getOrNull(),
                    "Validation stamp has not been created"
                )
            }
            // Checks the validation stamp & run for the job
            if (expectedJob) {
                assertNotNull(
                    structureService.findValidationStampByName(project.name, branch.name, expectedJobVsName).getOrNull(),
                    "Validation stamp for the job has been created"
                ) { vs ->
                    // Checks the validation run has been created
                    val runs = structureService.getValidationRunsForBuildAndValidationStamp(
                        id,
                        vs.id,
                        offset = 0,
                        count = 1
                    )
                    assertNotNull(runs.firstOrNull()) { run ->
                        assertEquals(expectedStatus, run.lastStatusId)
                        assertNotNull(runInfoService.getRunInfo(run), "Run info has been set") { info ->
                            assertEquals(60, info.runTime, "Run time = 60 seconds")
                            assertEquals("github-workflow", info.sourceType)
                            assertEquals("uri:job", info.sourceUri)
                        }
                        assertNotNull(getProperty(run, ValidationRunGitHubWorkflowJobPropertyType::class.java)) { p ->
                            assertEquals("build", p.job)
                            assertEquals("run-name", p.name)
                            assertEquals(1, p.runNumber)
                            assertEquals("uri:job", p.url)
                            assertEquals(false, p.running)
                        }
                    }
                }
            } else {
                assertNull(
                    structureService.findValidationStampByName(project.name, branch.name, expectedJobVsName).getOrNull(),
                    "Validation stamp for the job has not been created"
                )
            }
        }
    }

    private fun Build.setupTest(
        runId: Long = 1L,
        job: String = "build",
        step: String? = "Publishing to the repository",
        status: WorkflowJobStepStatus = WorkflowJobStepStatus.completed,
        conclusion: WorkflowJobStepConclusion? = WorkflowJobStepConclusion.success,
    ) {
        val ref = Time.now()
        asAdmin {
            // Git branch at branch level
            setProperty(
                branch,
                GitBranchConfigurationPropertyType::class.java,
                GitBranchConfigurationProperty(
                    branch = "main",
                    buildCommitLink = null, // Not used
                    isOverride = false,
                    buildTagInterval = 0,
                )
            )
            // Run ID for the build
            setProperty(
                this,
                BuildGitHubWorkflowRunPropertyType::class.java,
                BuildGitHubWorkflowRunProperty(
                    runId = runId,
                    url = "",
                    name = "run-name",
                    runNumber = 1,
                    running = true,
                )
            )
            workflowJobProcessingService.setupValidation(
                repository = Repository(
                    name = project.name,
                    description = null,
                    htmlUrl = "https://github.com/nemerosa/${project.name}",
                    owner = Owner(login = "nemerosa"),
                ),
                runId = runId,
                runAttempt = 1,
                job = job,
                jobUrl = "uri:job",
                step = step,
                status = status,
                conclusion = conclusion,
                startedAt = ref.minusMinutes(1),
                completedAt = ref,
            )
        }
    }

    @Test
    fun `Setup validation stamp when non existing`() {
        project {
            branch {
                asAdmin {
                    val setup = workflowJobProcessingService.setupValidationStamp(this, "VS", "Validation stamp name")
                    assertEquals("VS", setup.name)
                    assertEquals("Validation stamp name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup validation stamp when existing with null input description`() {
        project {
            branch {
                val vs = validationStamp("VS", description = "Validation stamp name")
                asAdmin {
                    val setup = workflowJobProcessingService.setupValidationStamp(this, "VS", null)
                    assertEquals(vs.id, setup.id)
                    assertEquals("VS", setup.name)
                    assertEquals("Validation stamp name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup validation stamp when existing with new input description`() {
        project {
            branch {
                val vs = validationStamp("VS", description = "Validation stamp name")
                asAdmin {
                    val setup = workflowJobProcessingService.setupValidationStamp(this, "VS", "Another description")
                    assertEquals(vs.id, setup.id)
                    assertEquals("VS", setup.name)
                    assertEquals("Another description", setup.description)
                    assertEquals(setup.description, structureService.getValidationStamp(vs.id).description)
                }
            }
        }
    }

    @Test
    fun `Setup promotion level when non existing`() {
        project {
            branch {
                asAdmin {
                    val setup = workflowJobProcessingService.setupPromotionLevel(this, "PL", "Promotion level name")
                    assertEquals("PL", setup.name)
                    assertEquals("Promotion level name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup promotion level when existing with null input description`() {
        project {
            branch {
                val pl = promotionLevel("PL", "Promotion level name")
                asAdmin {
                    val setup = workflowJobProcessingService.setupPromotionLevel(this, "PL", null)
                    assertEquals(pl.id, setup.id)
                    assertEquals("PL", setup.name)
                    assertEquals("Promotion level name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup promotion level when existing with new input description`() {
        project {
            branch {
                val pl = promotionLevel("PL", "Promotion level name")
                asAdmin {
                    val setup = workflowJobProcessingService.setupPromotionLevel(this, "PL", "Another description")
                    assertEquals(pl.id, setup.id)
                    assertEquals("PL", setup.name)
                    assertEquals("Another description", setup.description)
                    assertEquals(setup.description, structureService.getPromotionLevel(pl.id).description)
                }
            }
        }
    }

}