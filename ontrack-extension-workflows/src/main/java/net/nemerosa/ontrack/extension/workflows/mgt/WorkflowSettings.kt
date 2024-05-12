package net.nemerosa.ontrack.extension.workflows.mgt

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.Duration

data class WorkflowSettings(
    @APILabel("Retention duration")
    @APIDescription("Number of milliseconds before workflow instances are removed")
    val retentionDuration: Long = DEFAULT_RETENTION_DURATION,
) {
    companion object {
        val DEFAULT_RETENTION_DURATION = Duration.ofDays(14).toMillis()
    }
}
