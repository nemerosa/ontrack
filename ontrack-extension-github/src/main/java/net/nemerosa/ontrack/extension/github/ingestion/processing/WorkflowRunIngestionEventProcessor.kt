package net.nemerosa.ontrack.extension.github.ingestion.processing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.PullRequest
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.ontrackProjectName
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowRunIngestionEventProcessor(
    structureService: StructureService,
) : AbstractWorkflowIngestionEventProcessor<WorkflowRunPayload>(
    structureService
) {

    override val event: String = "workflow_run"

    override val payloadType: KClass<WorkflowRunPayload> = WorkflowRunPayload::class

    override fun process(payload: WorkflowRunPayload) {
        when (payload.action) {
            WorkflowRunAction.requested -> startBuild(payload)
        }
        // TODO Validation stamps & run --> for the run
        // TODO Validation runs links to the GitHub workflow --> for the run
        // TODO Validation runs run info --> for the run
    }

    private fun startBuild(payload: WorkflowRunPayload) {
        // Build creation & setup
        val build = getOrCreateBuild(payload)
    }

    private fun getOrCreateBuild(payload: WorkflowRunPayload): Build {
        // Gets or creates the project
        val project = getOrCreateProject(payload)
        // Branch creation & setup
        val branch = getOrCreateBranch(project, payload)
        // Build creation & setup
        val buildName = normalizeName(
            "${payload.workflowRun.name}-${payload.workflowRun.runNumber}"
        )
        val build = structureService.findBuildByName(project.name, branch.name, buildName)
            .getOrNull()
            ?: structureService.newBuild(
                Build.of(
                    branch,
                    nd(buildName, ""),
                    // TODO Use the payload for the signature
                    Signature.Companion.of("test")
                )
            )
        // TODO Link between the build and the workflow
        // OK
        return build
    }

    private fun getOrCreateBranch(project: Project, payload: WorkflowRunPayload): Branch {
        if (payload.workflowRun.isPullRequest()) {
            TODO("Pull requests are not supported yet.")
        } else {
            val gitBranch = payload.workflowRun.headBranch
            val branchName = normalizeName(gitBranch)
            val branch = structureService.findBranchByName(project.name, branchName)
                .getOrNull()
                ?: structureService.newBranch(
                    Branch.of(
                        project,
                        nd(
                            name = branchName,
                            description = "$gitBranch branch",
                        )
                    )
                )
            // TODO Setup the Git configuration for this branch
            // OK
            return branch
        }
    }

    private fun getOrCreateProject(payload: WorkflowRunPayload): Project {
        val name = payload.repository.ontrackProjectName
        val project = structureService.findProjectByName(name)
            .getOrNull()
            ?: structureService.newProject(
                Project.of(
                    nd(
                        name = name,
                        description = payload.repository.description,
                    )
                )
            )
        // TODO Setup the Git configuration for this project
        return project
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowRunPayload(
    val action: WorkflowRunAction,
    @JsonProperty("workflow_run")
    val workflowRun: WorkflowRun,
    repository: Repository,
) : AbstractWorkflowPayload(
    repository,
)

enum class WorkflowRunAction {
    requested,
    completed
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkflowRun(
    val name: String,
    @JsonProperty("run_number")
    val runNumber: Int,
    @JsonProperty("head_branch")
    val headBranch: String,
    @JsonProperty("pull_requests")
    val pullRequests: List<PullRequest>,
) {
    fun isPullRequest() = pullRequests.isNotEmpty()
}