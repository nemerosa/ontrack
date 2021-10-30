package net.nemerosa.ontrack.extension.github.ingestion.processing.job

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class BuildWithWorkflowRunIdNotFoundException(projectName: String, runId: Long) : NotFoundException(
    "No Ontrack build for workflow run ID = $runId in project $projectName was found."
)
