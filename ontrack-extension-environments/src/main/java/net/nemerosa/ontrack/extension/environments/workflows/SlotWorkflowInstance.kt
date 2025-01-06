package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleOverride
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import java.time.LocalDateTime
import java.util.*

data class SlotWorkflowInstance(
    val id: String = UUID.randomUUID().toString(),
    val start: LocalDateTime = Time.now,
    val pipeline: SlotPipeline,
    val slotWorkflow: SlotWorkflow,
    val workflowInstance: WorkflowInstance,
    val override: SlotAdmissionRuleOverride? = null,
)
