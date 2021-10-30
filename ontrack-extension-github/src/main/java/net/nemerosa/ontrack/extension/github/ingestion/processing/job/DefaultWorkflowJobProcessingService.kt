package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepConclusion
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.WorkflowJobStepStatus
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.getProjectName
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.workflow.BuildGitHubWorkflowRunPropertyType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class DefaultWorkflowJobProcessingService(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
) : WorkflowJobProcessingService {

    override fun setupValidation(
        owner: String,
        repository: String,
        runId: Long,
        job: String,
        step: String?,
        status: WorkflowJobStepStatus,
        conclusion: WorkflowJobStepConclusion?,
        startedAt: LocalDateTime?,
        completedAt: LocalDateTime?
    ) {
        // Project name
        val projectName = getProjectName(owner, repository)
        // Gets the build
        val build = findBuild(projectName, runId)
        // Name of the validation stamp
        val vsName = getValidationStampName(job, step)
    }

    private fun findBuild(projectName: String, runId: Long): Build? {
        val project = structureService.findProjectByName(projectName).getOrNull()
            ?: return null
        return propertyService.findByEntityTypeAndSearchArguments(
            ProjectEntityType.BUILD,
            BuildGitHubWorkflowRunPropertyType::class,
            PropertySearchArguments(
                jsonContext = null,
                jsonCriteria = "json->>'runId' = :runId",
                criteriaParams = mapOf(
                    "runId" to runId,
                )
            )
        ).map {
            structureService.getBuild(it)
        }.firstOrNull {
            it.project.id == project.id
        }
    }


    private fun getValidationStampName(job: String, step: String?): String {
        val baseName = if (step != null) {
            "$job-$step"
        } else {
            job
        }
        // TODO Mapping in the ingestion configuration
        return normalizeName(baseName)
    }

}