package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunProperty
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WorkflowJobProcessingServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowJobProcessingService: WorkflowJobProcessingService

    @Test
    fun `Creation of a simple validation run`() {
        val ref = Time.now()
        asAdmin {
            project {
                branch {
                    build {
                        setProperty(
                            this,
                            BuildGitHubWorkflowRunPropertyType::class.java,
                            BuildGitHubWorkflowRunProperty(
                                runId = 1L,
                                url = "",
                                name = "",
                                runNumber = 1,
                                running = true,
                            )
                        )
                        workflowJobProcessingService.setupValidation(
                            owner = "nemerosa",
                            repository = project.name,
                            runId = 1,
                            runAttempt = 1,
                            job = "build",
                            step = "Publishing to the repository",
                            status = WorkflowJobStepStatus.completed,
                            conclusion = WorkflowJobStepConclusion.success,
                            startedAt = ref.minusMinutes(1),
                            completedAt = ref,
                        )
                        // Checks the validation stamp has been created
                        val vsName = "build-publishing-to-the-repository"
                        assertNotNull(
                            structureService.findValidationStampByName(project.name, branch.name, vsName).getOrNull(),
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
                                assertEquals("PASSED", run.lastStatusId)
                            }
                        }
                    }
                }
            }
        }
    }

}