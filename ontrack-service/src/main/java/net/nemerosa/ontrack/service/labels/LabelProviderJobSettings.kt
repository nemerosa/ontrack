package net.nemerosa.ontrack.service.labels

/**
 * Configuration of the [LabelProviderJob] job.
 */
data class LabelProviderJobSettings(
        val enabled: Boolean,
        val interval: Int,
        val perProject: Boolean
)
