package net.nemerosa.ontrack.extension.av.settings

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.nemerosa.ontrack.json.SimpleDurationDeserializer
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.Duration

@APIDescription("Auto versioning settings")
data class AutoVersioningSettings(
    @APIDescription("""The "Auto versioning on promotion" feature is enabled only if this flag is set to `true`.""")
    val enabled: Boolean,
    @APIDescription("Maximum time to keep audit entries for non-running auto versioning requests")
    @JsonDeserialize(using = SimpleDurationDeserializer::class)
    val auditRetentionDuration: Duration = DEFAULT_AUDIT_RETENTION_DURATION,
    @APIDescription("Maximum time to keep audit entries for all kinds of auto versioning requests (counted _after_ the audit retention)")
    @JsonDeserialize(using = SimpleDurationDeserializer::class)
    val auditCleanupDuration: Duration = DEFAULT_AUDIT_CLEANUP_DURATION,
    @APIDescription("Creation of the build link on auto version check")
    @APILabel("Build links on auto versioning check")
    val buildLinks: Boolean = DEFAULT_BUILD_LINKS,
) {
    companion object {
        /**
         * Is the auto versioning enabled by default?
         */
        const val DEFAULT_ENABLED = true

        /**
         * Default value for [AutoVersioningSettings.auditRetentionDuration]
         */
        val DEFAULT_AUDIT_RETENTION_DURATION: Duration = Duration.ofDays(14)

        /**
         * Default value for [AutoVersioningSettings.auditCleanupDuration]
         */
        val DEFAULT_AUDIT_CLEANUP_DURATION: Duration = Duration.ofDays(90)

        /**
         * Default value for [AutoVersioningSettings.buildLinks]
         */
        val DEFAULT_BUILD_LINKS: Boolean = true
    }
}
