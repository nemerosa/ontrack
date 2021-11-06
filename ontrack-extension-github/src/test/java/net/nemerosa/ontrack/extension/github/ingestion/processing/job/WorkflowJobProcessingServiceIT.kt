package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.extension.github.workflow.ValidationRunGitHubWorkflowJobPropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.RunInfoService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WorkflowJobProcessingServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowJobProcessingService: WorkflowJobProcessingService

    @Autowired
    private lateinit var runInfoService: RunInfoService

    @Test
    fun `Creation of a simple validation run`() {
        project {
            branch {
                build {
                    test()
                }
            }
        }
    }

    @Test
    fun `Failed status`() {
        project {
            branch {
                build {
                    test(
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
                    test(runId = 1)
                }
                build {
                    test(runId = 2)
                }
            }
        }
    }

    private fun Build.test(
        runId: Long = 1L,
        job: String = "build",
        step: String? = "Publishing to the repository",
        status: WorkflowJobStepStatus = WorkflowJobStepStatus.completed,
        conclusion: WorkflowJobStepConclusion = WorkflowJobStepConclusion.success,
        expectedVsName: String = normalizeName("$job-$step"),
        expectedStatus: String = "PASSED",
    ) {
        val ref = Time.now()
        asAdmin {
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
                owner = "nemerosa",
                repository = project.name,
                runId = runId,
                runAttempt = 1,
                job = "build",
                jobUrl = "uri:job",
                step = "Publishing to the repository",
                status = status,
                conclusion = conclusion,
                startedAt = ref.minusMinutes(1),
                completedAt = ref,
            )
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
        }
    }

}