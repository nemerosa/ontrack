package net.nemerosa.ontrack.extension.av.settings

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.time.Duration

@APIDescription("Auto versioning settings")
data class AutoVersioningSettings(
    @APIDescription("""The "Auto versioning on promotion" feature is enabled only if this flag is set to `true`.""")
    val enabled: Boolean,
    @APIDescription("Maximum number of days to keep audit entries for non-running auto versioning requests")
    @JsonDeserialize(using = AutoVersioningSettingsDurationDeserializer::class)
    val auditRetentionDuration: Duration = DEFAULT_AUDIT_RETENTION_DURATION,
    @APIDescription("Maximum number of days to keep audit entries for all kinds of auto versioning requests (counted _after_ the audit retention)")
    @JsonDeserialize(using = AutoVersioningSettingsDurationDeserializer::class)
    val auditCleanupDuration: Duration = DEFAULT_AUDIT_CLEANUP_DURATION,
) {
    companion object {
        /**
         * Is the auto versioning enabled by default?
         */
        const val DEFAULT_ENABLED = false

        /**
         * Default value for [PRCreationSettings.auditRetentionDuration]
         */
        val DEFAULT_AUDIT_RETENTION_DURATION: Duration = Duration.ofDays(14)

        /**
         * Default value for [PRCreationSettings.auditCleanupDuration]
         */
        val DEFAULT_AUDIT_CLEANUP_DURATION: Duration = Duration.ofDays(90)
    }
}
