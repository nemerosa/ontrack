package net.nemerosa.ontrack.extension.workflows.ci

import com.fasterxml.jackson.annotation.JsonAnySetter
import net.nemerosa.ontrack.extension.workflows.definition.Workflow

data class WorkflowsBranchCIConfig(
    @field:JsonAnySetter
    val promotions: Map<String, List<Workflow>> = mutableMapOf()
)
